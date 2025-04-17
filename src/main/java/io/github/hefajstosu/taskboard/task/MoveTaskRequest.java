package io.github.hefajstosu.taskboard.task;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record MoveTaskRequest(@NotNull Long columnId, @NotNull @PositiveOrZero Integer position) {
}
