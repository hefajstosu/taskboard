package io.github.hefajstosu.taskboard.project; 

import io.github.hefajstosu.taskboard.common.NotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// 404 not 403
@Component
@Transactional(readOnly = true)
public class ProjectAccess {

    private final ProjectMemberRepository memberRepository;

    public ProjectAccess(ProjectMemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public ProjectMember requireMember(Long projectId, Long userId) {
        return memberRepository.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new NotFoundException("Project", projectId));
    }

    public ProjectMember requireOwner(Long projectId, Long userId) {
        ProjectMember member = requireMember(projectId, userId);
        if (!member.isOwner()){
            throw new AccessDeniedException("Only the project owner can do this");
        }
        return member;
    }
}
