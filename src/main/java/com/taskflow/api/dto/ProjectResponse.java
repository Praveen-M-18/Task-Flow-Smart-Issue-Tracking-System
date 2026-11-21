package com.taskflow.api.dto;

import com.taskflow.api.entity.Project;
import java.time.LocalDateTime;

public class ProjectResponse {

    private Long id;
    private String title;
    private String description;
    private UserResponse manager;
    private LocalDateTime createdAt;

    public ProjectResponse() {
    }

    public ProjectResponse(Long id, String title, String description, UserResponse manager, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.manager = manager;
        this.createdAt = createdAt;
    }

    public static ProjectResponse fromEntity(Project project) {
        if (project == null) return null;
        return new ProjectResponse(
                project.getId(),
                project.getTitle(),
                project.getDescription(),
                UserResponse.fromEntity(project.getManager()),
                project.getCreatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserResponse getManager() {
        return manager;
    }

    public void setManager(UserResponse manager) {
        this.manager = manager;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
