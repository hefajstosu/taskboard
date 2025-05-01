package io.github.hefajstosu.taskboard.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TaskRequest(
        @NotBlank @Size(max = 200) String title, 
        @Size(max = 4000) String description,
        Priority priority,
        Long assigneeId,
        LocalDate dueDate
){
}
