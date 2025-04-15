package io.github.hefajstosu.taskboard.project;

import java.time.Instant;

public record ProjectResponse(Long id, String name, String description, Instant createdAt) {

    static ProjectResponse from(Project project){
        return new ProjectResponse(project.getId(), project.getName(), project.getDescription(), project.getCreatedAt());
    }
}
