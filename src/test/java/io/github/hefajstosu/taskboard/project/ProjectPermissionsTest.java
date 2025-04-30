package io.github.hefajstosu.taskboard.project;

import io.github.hefajstosu.taskboard.user.User;
import io.github.hefajstosu.taskboard.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class ProjectPermissionsTest {

    @Autowired
    ProjectService projectService;

    @Autowired
    UserService userService;

    @Autowired
    ProjectRepository projectRepository;

    User owner;
    User member;
    Long projectId;

    @BeforeEach
    void setUp(){ 
        owner = userService.register("owner@example.com", "Owner", "password123");
        member = userService.register("member@example.com", "Member", "password123");
        projectId = projectService.create(new ProjectRequest("Secret plans", null), owner.getId()).id();
        projectService.addMember(projectId, new AddMemberRequest("member@example.com"), owner.getId());
    }

    @Test
    void memberCanSeeProject() {
        assertThat(projectService.findById(projectId, member.getId()).name()).isEqualTo("Secret plans");
        assertThat(projectService.members(projectId, member.getId())).hasSize(2);
    }

    @Test
    void memberCannotDeleteProject() {
        assertThatThrownBy(() -> projectService.delete(projectId, member.getId()))
                .isInstanceOf(AccessDeniedException.class); 
        assertThat(projectRepository.existsById(projectId)).isTrue();
    }

    @Test
    void memberCannotInviteOthers() {
        userService.register("third@example.com", "Third", "password123");

        assertThatThrownBy(() -> projectService.addMember(projectId, new AddMemberRequest("third@example.com"), member.getId())) 
                .isInstanceOf(AccessDeniedException.class); 
    }

    @Test
    void ownerCanDeleteProject() {
        projectService.delete(projectId, owner.getId());

        assertThat(projectRepository.existsById(projectId)).isFalse();
    }
}
