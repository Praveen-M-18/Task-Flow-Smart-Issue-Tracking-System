package com.taskflow.api.dto;

public class DashboardMetricsResponse {

    private long totalProjects;
    private long totalTasks;
    private long completedTasks;
    private long pendingTasks;
    private long testingTasks;
    private long criticalTasks;

    public DashboardMetricsResponse() {
    }

    public DashboardMetricsResponse(long totalProjects, long totalTasks, long completedTasks,
                                    long pendingTasks, long testingTasks, long criticalTasks) {
        this.totalProjects = totalProjects;
        this.totalTasks = totalTasks;
        this.completedTasks = completedTasks;
        this.pendingTasks = pendingTasks;
        this.testingTasks = testingTasks;
        this.criticalTasks = criticalTasks;
    }

    public long getTotalProjects() {
        return totalProjects;
    }

    public void setTotalProjects(long totalProjects) {
        this.totalProjects = totalProjects;
    }

    public long getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(long totalTasks) {
        this.totalTasks = totalTasks;
    }

    public long getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(long completedTasks) {
        this.completedTasks = completedTasks;
    }

    public long getPendingTasks() {
        return pendingTasks;
    }

    public void setPendingTasks(long pendingTasks) {
        this.pendingTasks = pendingTasks;
    }

    public long getTestingTasks() {
        return testingTasks;
    }

    public void setTestingTasks(long testingTasks) {
        this.testingTasks = testingTasks;
    }

    public long getCriticalTasks() {
        return criticalTasks;
    }

    public void setCriticalTasks(long criticalTasks) {
        this.criticalTasks = criticalTasks;
    }
}
