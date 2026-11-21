package com.taskflow.api.dto;

import com.taskflow.api.entity.Status;
import jakarta.validation.constraints.NotNull;

public class UpdateTaskStatusRequest {

    @NotNull(message = "Status is required")
    private Status status;

    public UpdateTaskStatusRequest() {
    }

    public UpdateTaskStatusRequest(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
