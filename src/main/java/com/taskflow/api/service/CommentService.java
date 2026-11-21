package com.taskflow.api.service;

import com.taskflow.api.dto.CommentResponse;
import com.taskflow.api.dto.CreateCommentRequest;
import com.taskflow.api.entity.Comment;
import com.taskflow.api.entity.Task;
import com.taskflow.api.entity.User;
import com.taskflow.api.exception.ResourceNotFoundException;
import com.taskflow.api.repository.CommentRepository;
import com.taskflow.api.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final ActivityLogService activityLogService;

    public CommentService(CommentRepository commentRepository,
                          TaskRepository taskRepository,
                          ActivityLogService activityLogService) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.activityLogService = activityLogService;
    }

    @Transactional
    public CommentResponse addComment(Long taskId, CreateCommentRequest request, User currentUser) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        Comment comment = new Comment();
        comment.setTask(task);
        comment.setUser(currentUser);
        comment.setText(request.getText());

        Comment savedComment = commentRepository.save(comment);

        activityLogService.logActivity(task, "Comment added by " + currentUser.getEmail());

        return CommentResponse.fromEntity(savedComment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByTaskId(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Task not found with id: " + taskId);
        }
        return commentRepository.findByTaskIdOrderByCreatedAtAsc(taskId).stream()
                .map(CommentResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
