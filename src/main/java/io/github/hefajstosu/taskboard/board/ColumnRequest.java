package io.github.hefajstosu.taskboard.board;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size; 

public record ColumnRequest(@NotBlank @Size(max = 50) String name, @Positive Integer wipLimit){
}
