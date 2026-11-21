package com.taskflow.api.service;

import com.taskflow.api.dto.DashboardMetricsResponse;
import com.taskflow.api.entity.Priority;
import com.taskflow.api.entity.Status;
import com.taskflow.api.repository.ProjectRepository;
import com.taskflow.api.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
public class DashboardService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public DashboardService(ProjectRepository projectRepository, TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional(readOnly = true)
    public DashboardMetricsResponse getDashboardMetrics() {
        long totalProjects = projectRepository.count();
        long totalTasks = taskRepository.count();

        long completedTasks = taskRepository.countByStatusIn(Arrays.asList(Status.DONE, Status.CLOSED));
        long pendingTasks = taskRepository.countByStatusIn(Arrays.asList(Status.TODO, Status.IN_PROGRESS));
        long testingTasks = taskRepository.countByStatusIn(Arrays.asList(Status.TESTING));
        long criticalTasks = taskRepository.countByPriority(Priority.CRITICAL);

        return new DashboardMetricsResponse(
                totalProjects,
                totalTasks,
                completedTasks,
                pendingTasks,
                testingTasks,
                criticalTasks
        );
    }
}
