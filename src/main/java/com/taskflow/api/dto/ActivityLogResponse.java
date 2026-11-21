package com.taskflow.api.dto;

import com.taskflow.api.entity.ActivityLog;
import java.time.LocalDateTime;

public class ActivityLogResponse {

    private Long id;
    private Long taskId;
    private String message;
    private LocalDateTime timestamp;

    public ActivityLogResponse() {
    }

    public ActivityLogResponse(Long id, Long taskId, String message, LocalDateTime timestamp) {
        this.id = id;
        this.taskId = taskId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public static ActivityLogResponse fromEntity(ActivityLog log) {
        if (log == null) return null;
        return new ActivityLogResponse(
                log.getId(),
                log.getTask() != null ? log.getTask().getId() : null,
                log.getMessage(),
                log.getTimestamp()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
