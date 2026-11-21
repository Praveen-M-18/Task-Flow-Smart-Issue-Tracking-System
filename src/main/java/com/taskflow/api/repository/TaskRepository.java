package com.taskflow.api.repository;

import com.taskflow.api.entity.Priority;
import com.taskflow.api.entity.Role;
import com.taskflow.api.entity.Status;
import com.taskflow.api.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssigneeId(Long assigneeId);
    List<Task> findByProjectId(Long projectId);

    long countByAssigneeIdAndStatusIn(Long assigneeId, Collection<Status> statuses);
    long countByStatusIn(Collection<Status> statuses);
    long countByPriority(Priority priority);

    /**
     * Workload Allocation Engine:
     * Returns the developer (Role.DEVELOPER) with the fewest active tasks
     * (excluding DONE and CLOSED statuses), ordered ascending by active task count.
     * Returns an Optional so the caller can handle no-developer case gracefully.
     */
    @Query("""
            SELECT t.assignee.id FROM Task t
            WHERE t.assignee IS NOT NULL
              AND t.assignee.role = :role
              AND t.status NOT IN :excludedStatuses
            GROUP BY t.assignee.id
            ORDER BY COUNT(t.id) ASC
            LIMIT 1
            """)
    Optional<Long> findLeastLoadedDeveloperIdExcludingStatuses(
            @Param("role") Role role,
            @Param("excludedStatuses") Collection<Status> excludedStatuses
    );

    /**
     * Performance Report Query:
     * Returns [userId, total_tasks, in_progress_count, completed_count]
     * for all DEVELOPER users (including those with zero tasks).
     */
    @Query("""
            SELECT
                u.id,
                COUNT(t.id),
                SUM(CASE WHEN t.status = com.taskflow.api.entity.Status.IN_PROGRESS THEN 1 ELSE 0 END),
                SUM(CASE WHEN t.status IN (com.taskflow.api.entity.Status.DONE, com.taskflow.api.entity.Status.CLOSED) THEN 1 ELSE 0 END)
            FROM User u
            LEFT JOIN Task t ON t.assignee.id = u.id
            WHERE u.role = :role
            GROUP BY u.id
            """)
    List<Object[]> findDeveloperTaskStats(@Param("role") Role role);
}

