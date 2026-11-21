package com.taskflow.api.controller;

import com.taskflow.api.dto.*;
import com.taskflow.api.entity.User;
import com.taskflow.api.service.ActivityLogService;
import com.taskflow.api.service.TaskService;
import com.taskflow.api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;
    private final ActivityLogService activityLogService;

    public TaskController(TaskService taskService,
                          UserService userService,
                          ActivityLogService activityLogService) {
        this.taskService = taskService;
        this.userService = userService;
        this.activityLogService = activityLogService;
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody CreateTaskRequest request) {
        User currentUser = userService.getCurrentUserEntity();
        TaskResponse response = taskService.createTask(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @GetMapping("/my-tasks")
    public ResponseEntity<List<TaskResponse>> getMyTasks() {
        User currentUser = userService.getCurrentUserEntity();
        return ResponseEntity.ok(taskService.getTasksByAssignee(currentUser.getId()));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TaskResponse>> getTasksByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(taskService.getTasksByProject(projectId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(@PathVariable Long id,
                                                         @Valid @RequestBody UpdateTaskStatusRequest request) {
        User currentUser = userService.getCurrentUserEntity();
        TaskResponse response = taskService.updateTaskStatus(id, request, currentUser);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/priority")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<TaskResponse> updateTaskPriority(@PathVariable Long id,
                                                           @Valid @RequestBody UpdateTaskPriorityRequest request) {
        User currentUser = userService.getCurrentUserEntity();
        TaskResponse response = taskService.updateTaskPriority(id, request, currentUser);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<TaskResponse> assignTask(@PathVariable Long id,
                                                   @Valid @RequestBody AssignTaskRequest request) {
        User currentUser = userService.getCurrentUserEntity();
        TaskResponse response = taskService.assignTask(id, request, currentUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/smart-allocation")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<List<SmartAllocationResponse>> getSmartAllocation() {
        return ResponseEntity.ok(taskService.getSmartAllocation());
    }

    /**
     * Workload Allocation Engine:
     * Returns the developer ID of the least-loaded active developer.
     * Excludes DONE and CLOSED tasks from workload count.
     */
    @GetMapping("/least-loaded-developer")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getLeastLoadedDeveloper() {
        Optional<Long> devId = taskService.getLeastLoadedDeveloperId();
        if (devId.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "message", "No active developers found with assigned tasks.",
                    "developerId", (Object) null
            ));
        }
        return ResponseEntity.ok(Map.of(
                "developerId", devId.get(),
                "message", "Least-loaded developer returned for automated assignment."
        ));
    }

    @GetMapping("/{id}/logs")
    public ResponseEntity<List<ActivityLogResponse>> getTaskLogs(@PathVariable Long id) {
        return ResponseEntity.ok(activityLogService.getLogsByTaskId(id));
    }
}

