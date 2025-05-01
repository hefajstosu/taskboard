package io.github.hefajstosu.taskboard.comment;

import io.github.hefajstosu.taskboard.user.UserSummary;

import java.time.Instant;

public record CommentResponse(Long id, UserSummary author, String body, Instant createdAt) {
    static CommentResponse from(Comment comment){
        return new CommentResponse(comment.getId(), UserSummary.of(comment.getAuthor()), comment.getBody(),
                comment.getCreatedAt());
    }
}
