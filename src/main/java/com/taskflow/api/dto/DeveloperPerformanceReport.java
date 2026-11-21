package com.taskflow.api.dto;

/**
 * DTO representing per-developer performance metrics for the export report.
 * All resolution time fields are in fractional hours.
 */
public class DeveloperPerformanceReport {

    private Long developerId;
    private String developerName;
    private String developerEmail;

    /** Total tasks ever assigned to this developer. */
    private long totalTasksAssigned;

    /** Tasks currently in IN_PROGRESS state. */
    private long inProgressTasks;

    /** Tasks in DONE or CLOSED state. */
    private long completedTasks;

    /**
     * Accuracy / Completion Metric:
     * (completedTasks / totalTasksAssigned) * 100, or 0 if no tasks assigned.
     */
    private double completionRatePercent;

    /** Average time from task creation to completion (DONE/CLOSED), in hours. */
    private double avgResolutionTimeHours;

    /** Average time from task creation to completion, in fractional days (hours / 24). */
    private double avgResolutionTimeDays;

    public DeveloperPerformanceReport() {
    }

    public DeveloperPerformanceReport(Long developerId, String developerName, String developerEmail,
                                      long totalTasksAssigned, long inProgressTasks, long completedTasks,
                                      double completionRatePercent,
                                      double avgResolutionTimeHours, double avgResolutionTimeDays) {
        this.developerId = developerId;
        this.developerName = developerName;
        this.developerEmail = developerEmail;
        this.totalTasksAssigned = totalTasksAssigned;
        this.inProgressTasks = inProgressTasks;
        this.completedTasks = completedTasks;
        this.completionRatePercent = completionRatePercent;
        this.avgResolutionTimeHours = avgResolutionTimeHours;
        this.avgResolutionTimeDays = avgResolutionTimeDays;
    }

    public Long getDeveloperId() { return developerId; }
    public void setDeveloperId(Long developerId) { this.developerId = developerId; }

    public String getDeveloperName() { return developerName; }
    public void setDeveloperName(String developerName) { this.developerName = developerName; }

    public String getDeveloperEmail() { return developerEmail; }
    public void setDeveloperEmail(String developerEmail) { this.developerEmail = developerEmail; }

    public long getTotalTasksAssigned() { return totalTasksAssigned; }
    public void setTotalTasksAssigned(long totalTasksAssigned) { this.totalTasksAssigned = totalTasksAssigned; }

    public long getInProgressTasks() { return inProgressTasks; }
    public void setInProgressTasks(long inProgressTasks) { this.inProgressTasks = inProgressTasks; }

    public long getCompletedTasks() { return completedTasks; }
    public void setCompletedTasks(long completedTasks) { this.completedTasks = completedTasks; }

    public double getCompletionRatePercent() { return completionRatePercent; }
    public void setCompletionRatePercent(double completionRatePercent) { this.completionRatePercent = completionRatePercent; }

    public double getAvgResolutionTimeHours() { return avgResolutionTimeHours; }
    public void setAvgResolutionTimeHours(double avgResolutionTimeHours) { this.avgResolutionTimeHours = avgResolutionTimeHours; }

    public double getAvgResolutionTimeDays() { return avgResolutionTimeDays; }
    public void setAvgResolutionTimeDays(double avgResolutionTimeDays) { this.avgResolutionTimeDays = avgResolutionTimeDays; }
}
