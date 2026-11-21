package com.taskflow.api.dto;

import com.taskflow.api.entity.Comment;
import java.time.LocalDateTime;

public class CommentResponse {

    private Long id;
    private Long taskId;
    private UserResponse user;
    private String text;
    private LocalDateTime createdAt;

    public CommentResponse() {
    }

    public CommentResponse(Long id, Long taskId, UserResponse user, String text, LocalDateTime createdAt) {
        this.id = id;
        this.taskId = taskId;
        this.user = user;
        this.text = text;
        this.createdAt = createdAt;
    }

    public static CommentResponse fromEntity(Comment comment) {
        if (comment == null) return null;
        return new CommentResponse(
                comment.getId(),
                comment.getTask() != null ? comment.getTask().getId() : null,
                UserResponse.fromEntity(comment.getUser()),
                comment.getText(),
                comment.getCreatedAt()
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

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
