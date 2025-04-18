package io.github.hefajstosu.taskboard.board;

public sealed interface WipCheck {
    record Allowed() implements WipCheck {
    }

    record LimitReached(String column, int limit) implements WipCheck {
    }

    static WipCheck of(BoardColumn column, int currentTaskCount) {
        Integer limit = column.getWipLimit();
        if(limit != null && currentTaskCount >= limit) {
            return new LimitReached(column.getName(), limit);
        }
        return new Allowed();
    }
}
