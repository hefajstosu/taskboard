package io.github.hefajstosu.taskboard.board;

import io.github.hefajstosu.taskboard.task.TaskResponse;

import java.util.List;

// for the ui
public record BoardResponse(Long projectId, String projectName, List<Column> columns) {


    public record Column(Long id, String name, int position, Integer wipLimit, List<TaskResponse> tasks) { 
    }
}
