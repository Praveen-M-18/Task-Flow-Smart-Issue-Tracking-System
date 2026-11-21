package com.taskflow.api.dto;

import com.taskflow.api.entity.Priority;
import jakarta.validation.constraints.NotNull;

public class UpdateTaskPriorityRequest {

    @NotNull(message = "Priority is required")
    private Priority priority;

    public UpdateTaskPriorityRequest() {
    }

    public UpdateTaskPriorityRequest(Priority priority) {
        this.priority = priority;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }
}
