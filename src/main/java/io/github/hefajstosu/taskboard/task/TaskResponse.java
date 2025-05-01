package io.github.hefajstosu.taskboard.task;

import io.github.hefajstosu.taskboard.user.UserSummary;

import java.time.Instant;
import java.time.LocalDate; 

public record TaskResponse(
        Long id,
        Long columnId,
        String title,
        String description,
        Priority priority,
        int position,
        UserSummary assignee,
        LocalDate dueDate,
        Instant createdAt
) {

    static TaskResponse from(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getColumn().getId(),
                task.getTitle(),
                task.getDescription(),
                task.getPriority(),
                task.getPosition(),
                UserSummary.of(task.getAssignee()),
                task.getDueDate(),
                task.getCreatedAt()
        ); 
    }
}
