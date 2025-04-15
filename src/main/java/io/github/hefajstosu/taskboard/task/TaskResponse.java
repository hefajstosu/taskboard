package io.github.hefajstosu.taskboard.task;

import java.time.Instant;

public record TaskResponse(
        Long id,
        Long columnId,
        String title,
        String description,
        Priority priority,
        int position,
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
                task.getCreatedAt()
        ); 
    }
}
