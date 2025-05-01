package io.github.hefajstosu.taskboard.user;

public record UserSummary(Long id, String displayName) {

    public static UserSummary of(User user) {
        return user == null ? null : new UserSummary(user.getId(), user.getDisplayName());
    }
}
