package io.github.hefajstosu.taskboard.task;

import io.github.hefajstosu.taskboard.board.BoardColumnService;
import io.github.hefajstosu.taskboard.board.ColumnRequest;
import io.github.hefajstosu.taskboard.project.ProjectRequest;
import io.github.hefajstosu.taskboard.project.ProjectService;
import io.github.hefajstosu.taskboard.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional; 

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class TaskSearchTest {

    @Autowired
    UserService userService;
    @Autowired
    ProjectService projectService;
    @Autowired
    BoardColumnService columnService;
    @Autowired
    TaskService taskService;

    Long userId;
    Long projectId;

    @BeforeEach
    void setUp() {
        userId = userService.register("search@example.com", "Searcher", "password123").getId();
        projectId = projectService.create(new ProjectRequest("Search", null), userId).id();
        Long todo = columnService.create(projectId, new ColumnRequest("To do", null), userId).id();
        Long doing = columnService.create(projectId, new ColumnRequest("Doing", null), userId).id();
        taskService.create(todo, new TaskRequest("Write docs", null, Priority.LOW, null, null), userId);
        taskService.create(todo, new TaskRequest("Fix login bug", null, Priority.HIGH, userId, LocalDate.of(2024, 10, 1)), userId);
        taskService.create(doing, new TaskRequest("Fix CI", null, Priority.HIGH, null, LocalDate.of(2024, 12, 1)), userId);


        // other project
        Long otherUser = userService.register("other@example.com", "Other", "password123").getId();
        Long otherProject = projectService.create(new ProjectRequest("Other", null), otherUser).id();
        Long otherColumn = columnService.create(otherProject, new ColumnRequest("To do", null), otherUser).id();
        taskService.create(otherColumn, new TaskRequest("Fix other stuff", null, Priority.HIGH, null, null), otherUser);
    }

    @Test
    void searchesAcrossColumnsOfProject() {
        var page = taskService.search(projectId, new TaskFilter(null, null, null, null), PageRequest.of(0, 10), userId);

        assertThat(page.getTotalElements()).isEqualTo(3);
    }

    @Test
    void filtersByTitleAndPriority() {
        var page = taskService.search(projectId, new TaskFilter(null, Priority.HIGH, "fix", null), PageRequest.of(0, 10), userId);

        assertThat(page.getContent()).extracting(TaskResponse::title)
                    .containsExactlyInAnyOrder("Fix login bug", "Fix CI");
    }

    @Test
    void filtersByAssigneeAndDueDate() {
        var byAssignee = taskService.search(projectId, new TaskFilter(userId, null, null, null), PageRequest.of(0, 10), userId);
        var dueSoon = taskService.search(projectId, new TaskFilter(null, null, null, LocalDate.of(2024, 10, 31)), PageRequest.of(0, 10), userId);

        assertThat(byAssignee.getContent()).extracting(TaskResponse::title).containsExactly("Fix login bug");
        assertThat(dueSoon.getContent()).extracting(TaskResponse::title).containsExactly("Fix login bug");
    }

    @Test
    void paginates() {
        var page = taskService.search(projectId, new TaskFilter(null, null, null, null), PageRequest.of(1, 2), userId);

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getTotalPages()).isEqualTo(2);
    }
}
