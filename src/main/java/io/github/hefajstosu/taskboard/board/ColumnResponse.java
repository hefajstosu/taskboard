package io.github.hefajstosu.taskboard.board;

public record ColumnResponse(Long id, String name, int position) {

    static ColumnResponse from(BoardColumn column) {
        return new ColumnResponse(column.getId(), column.getName(), column.getPosition());
    }
}
