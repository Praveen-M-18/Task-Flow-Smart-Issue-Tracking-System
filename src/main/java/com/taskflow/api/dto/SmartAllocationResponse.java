package com.taskflow.api.dto;

public class SmartAllocationResponse {

    private UserResponse developer;
    private long activeTasksCount;

    public SmartAllocationResponse() {
    }

    public SmartAllocationResponse(UserResponse developer, long activeTasksCount) {
        this.developer = developer;
        this.activeTasksCount = activeTasksCount;
    }

    public UserResponse getDeveloper() {
        return developer;
    }

    public void setDeveloper(UserResponse developer) {
        this.developer = developer;
    }

    public long getActiveTasksCount() {
        return activeTasksCount;
    }

    public void setActiveTasksCount(long activeTasksCount) {
        this.activeTasksCount = activeTasksCount;
    }
}
