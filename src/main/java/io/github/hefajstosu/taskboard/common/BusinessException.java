package io.github.hefajstosu.taskboard.common;

// -> 422
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
