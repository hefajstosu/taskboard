package io.github.hefajstosu.taskboard.project; 

public record MemberResponse(Long userId, String email, String displayName, ProjectRole role) {

    static MemberResponse from(ProjectMember member){
        var user = member.getUser();
        return new MemberResponse(user.getId(), user.getEmail(), user.getDisplayName(), member.getRole());
    }
}
