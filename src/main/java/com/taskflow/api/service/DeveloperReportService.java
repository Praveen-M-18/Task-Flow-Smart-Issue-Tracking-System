package com.taskflow.api.service;

import com.taskflow.api.dto.DeveloperPerformanceReport;
import com.taskflow.api.entity.Role;
import com.taskflow.api.entity.Status;
import com.taskflow.api.entity.Task;
import com.taskflow.api.entity.User;
import com.taskflow.api.repository.TaskRepository;
import com.taskflow.api.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DeveloperReportService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public DeveloperReportService(UserRepository userRepository, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    /**
     * Builds a list of DeveloperPerformanceReport for all DEVELOPER-role users.
     * Calculates:
     *   - Total tasks assigned
     *   - In-progress task count
     *   - Completed (DONE + CLOSED) task count
     *   - Completion rate: (completed / total) * 100
     *   - Average resolution time in hours and days (createdAt -> updatedAt for DONE/CLOSED tasks)
     */
    @Transactional(readOnly = true)
    public List<DeveloperPerformanceReport> buildPerformanceReport() {
        List<User> developers = userRepository.findByRole(Role.DEVELOPER);

        return developers.stream().map(dev -> {
            List<Task> allTasks = taskRepository.findByAssigneeId(dev.getId());

            long total = allTasks.size();
            long inProgress = allTasks.stream()
                    .filter(t -> t.getStatus() == Status.IN_PROGRESS)
                    .count();
            long completed = allTasks.stream()
                    .filter(t -> t.getStatus() == Status.DONE || t.getStatus() == Status.CLOSED)
                    .count();

            double completionRate = total == 0 ? 0.0 : ((double) completed / total) * 100.0;

            // Average resolution time: from task creation to last update for DONE/CLOSED tasks
            OptionalDouble avgHours = allTasks.stream()
                    .filter(t -> t.getStatus() == Status.DONE || t.getStatus() == Status.CLOSED)
                    .filter(t -> t.getCreatedAt() != null && t.getUpdatedAt() != null)
                    .mapToDouble(t -> {
                        Duration duration = Duration.between(t.getCreatedAt(), t.getUpdatedAt());
                        return duration.toMinutes() / 60.0;
                    })
                    .average();

            double avgResolutionHours = avgHours.orElse(0.0);
            double avgResolutionDays = avgResolutionHours / 24.0;

            return new DeveloperPerformanceReport(
                    dev.getId(),
                    dev.getName(),
                    dev.getEmail(),
                    total,
                    inProgress,
                    completed,
                    Math.round(completionRate * 100.0) / 100.0,
                    Math.round(avgResolutionHours * 100.0) / 100.0,
                    Math.round(avgResolutionDays * 100.0) / 100.0
            );
        }).collect(Collectors.toList());
    }

    /**
     * Serializes the developer performance report as a CSV string.
     * Columns: ID, Name, Email, Total Tasks, In-Progress, Completed, Completion Rate (%), Avg Resolution (hours), Avg Resolution (days)
     */
    @Transactional(readOnly = true)
    public String generateCsvReport() {
        List<DeveloperPerformanceReport> reports = buildPerformanceReport();

        StringWriter sw = new StringWriter();
        // CSV Header
        sw.append("Developer ID,Developer Name,Email,Total Tasks Assigned,In-Progress Tasks,Completed Tasks,Completion Rate (%),Avg Resolution Time (Hours),Avg Resolution Time (Days)\n");

        for (DeveloperPerformanceReport r : reports) {
            sw.append(String.valueOf(r.getDeveloperId())).append(",");
            sw.append(escapeCsvField(r.getDeveloperName())).append(",");
            sw.append(escapeCsvField(r.getDeveloperEmail())).append(",");
            sw.append(String.valueOf(r.getTotalTasksAssigned())).append(",");
            sw.append(String.valueOf(r.getInProgressTasks())).append(",");
            sw.append(String.valueOf(r.getCompletedTasks())).append(",");
            sw.append(String.format("%.2f", r.getCompletionRatePercent())).append(",");
            sw.append(String.format("%.2f", r.getAvgResolutionTimeHours())).append(",");
            sw.append(String.format("%.2f", r.getAvgResolutionTimeDays())).append("\n");
        }

        return sw.toString();
    }

    /** Escapes a CSV field by wrapping in quotes if it contains commas, quotes, or newlines. */
    private String escapeCsvField(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
