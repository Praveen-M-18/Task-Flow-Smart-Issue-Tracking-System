package com.taskflow.api.service;

import com.taskflow.api.dto.ActivityLogResponse;
import com.taskflow.api.entity.ActivityLog;
import com.taskflow.api.entity.Task;
import com.taskflow.api.repository.ActivityLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public ActivityLogService(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    @Transactional
    public void logActivity(Task task, String message) {
        ActivityLog log = new ActivityLog(task, message);
        activityLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<ActivityLogResponse> getLogsByTaskId(Long taskId) {
        return activityLogRepository.findByTaskIdOrderByTimestampDesc(taskId).stream()
                .map(ActivityLogResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
