package com.taskflow.api.service;

import com.taskflow.api.dto.*;
import com.taskflow.api.entity.*;
import com.taskflow.api.exception.BadRequestException;
import com.taskflow.api.exception.ResourceNotFoundException;
import com.taskflow.api.exception.UnauthorizedAccessException;
import com.taskflow.api.repository.ProjectRepository;
import com.taskflow.api.repository.TaskRepository;
import com.taskflow.api.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ActivityLogService activityLogService;

    private static final List<Status> ACTIVE_STATUSES = Arrays.asList(
            Status.TODO, Status.IN_PROGRESS, Status.TESTING
    );

    public TaskService(TaskRepository taskRepository,
                       UserRepository userRepository,
                       ProjectRepository projectRepository,
                       ActivityLogService activityLogService) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.activityLogService = activityLogService;
    }

    @Transactional
    public TaskResponse createTask(CreateTaskRequest request, User currentUser) {
        // --- Strict Project Validation ---
        if (request.getProjectId() == null) {
            throw new BadRequestException("Project ID is required. A task cannot exist without a valid project.");
        }
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + request.getProjectId()));

        User assignee = null;
        if (request.getAssigneeId() != null) {
            assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assignee not found with id: " + request.getAssigneeId()));
        }

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setStatus(Status.TODO);
        task.setProject(project);
        task.setAssignee(assignee);
        task.setDueDate(request.getDueDate());

        Task savedTask = taskRepository.save(task);

        String assigneeName = assignee != null ? assignee.getName() : "Unassigned";
        activityLogService.logActivity(savedTask, "Task created by " + currentUser.getEmail() + " and assigned to " + assigneeName);

        return mapToResponse(savedTask);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        return mapToResponse(task);
    }

    @Transactional(readOnly = true)
    public Task getTaskEntityById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByAssignee(Long assigneeId) {
        return taskRepository.findByAssigneeId(assigneeId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByProject(Long projectId) {
        return taskRepository.findByProjectId(projectId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public TaskResponse updateTaskStatus(Long taskId, UpdateTaskStatusRequest request, User currentUser) {
        Task task = getTaskEntityById(taskId);

        // Security validation for DEVELOPER role: developers can only update their assigned tasks
        if (currentUser.getRole() == Role.DEVELOPER) {
            if (task.getAssignee() == null || !task.getAssignee().getId().equals(currentUser.getId())) {
                throw new UnauthorizedAccessException("Developers can only update status for tasks assigned to them");
            }
        }

        Status oldStatus = task.getStatus();
        Status newStatus = request.getStatus();

        task.setStatus(newStatus);
        Task updatedTask = taskRepository.save(task);

        activityLogService.logActivity(updatedTask,
                "Status changed from " + oldStatus + " to " + newStatus + " by " + currentUser.getEmail());

        return mapToResponse(updatedTask);
    }

    @Transactional
    public TaskResponse updateTaskPriority(Long taskId, UpdateTaskPriorityRequest request, User currentUser) {
        Task task = getTaskEntityById(taskId);

        Priority oldPriority = task.getPriority();
        Priority newPriority = request.getPriority();

        task.setPriority(newPriority);
        Task updatedTask = taskRepository.save(task);

        activityLogService.logActivity(updatedTask,
                "Priority updated from " + oldPriority + " to " + newPriority + " by " + currentUser.getEmail());

        return mapToResponse(updatedTask);
    }

    @Transactional
    public TaskResponse assignTask(Long taskId, AssignTaskRequest request, User currentUser) {
        Task task = getTaskEntityById(taskId);

        User assignee = userRepository.findById(request.getAssigneeId())
                .orElseThrow(() -> new ResourceNotFoundException("Assignee not found with id: " + request.getAssigneeId()));

        task.setAssignee(assignee);
        Task updatedTask = taskRepository.save(task);

        activityLogService.logActivity(updatedTask,
                "Task assigned to " + assignee.getName() + " (" + assignee.getEmail() + ") by " + currentUser.getEmail());

        return mapToResponse(updatedTask);
    }

    /**
     * Smart Allocation Endpoint Logic:
     * Query developers (Role DEVELOPER) where activeTasks < 5, sorted by activeTasks ASC.
     */
    @Transactional(readOnly = true)
    public List<SmartAllocationResponse> getSmartAllocation() {
        List<User> developers = userRepository.findByRole(Role.DEVELOPER);

        return developers.stream()
                .map(dev -> {
                    long activeCount = taskRepository.countByAssigneeIdAndStatusIn(dev.getId(), ACTIVE_STATUSES);
                    return new SmartAllocationResponse(UserResponse.fromEntity(dev), activeCount);
                })
                .filter(res -> res.getActiveTasksCount() < 5)
                .sorted(Comparator.comparingLong(SmartAllocationResponse::getActiveTasksCount))
                .collect(Collectors.toList());
    }

    /**
     * Workload Allocation Engine:
     * Returns the ID of the DEVELOPER with the fewest active tasks (excluding DONE/CLOSED).
     * Used for automated task assignment to dynamically balance team bandwidth.
     */
    @Transactional(readOnly = true)
    public Optional<Long> getLeastLoadedDeveloperId() {
        List<Status> excludedStatuses = Arrays.asList(Status.DONE, Status.CLOSED);
        return taskRepository.findLeastLoadedDeveloperIdExcludingStatuses(Role.DEVELOPER, excludedStatuses);
    }

    private TaskResponse mapToResponse(Task task) {
        TaskResponse response = TaskResponse.fromEntity(task);
        if (task != null) {
            Project project = task.getProject();
            User assignee = task.getAssignee();
            response.setProjectId(project != null ? project.getId() : null);
            response.setProjectName(project != null ? project.getTitle() : null);
            response.setAssignedToId(assignee != null ? assignee.getId() : null);
            response.setAssignedToName(assignee != null ? assignee.getName() : null);
        }
        return response;
    }
}

