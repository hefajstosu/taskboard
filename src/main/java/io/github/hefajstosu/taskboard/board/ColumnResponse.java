package io.github.hefajstosu.taskboard.board;

public record ColumnResponse(Long id, String name, int position, Integer wipLimit) {
    static ColumnResponse from(BoardColumn column) {
        return new ColumnResponse(column.getId(), column.getName(), column.getPosition(), column.getWipLimit());
    }
}
