package io.github.hefajstosu.taskboard.board;

import io.github.hefajstosu.taskboard.project.ProjectRequest;
import io.github.hefajstosu.taskboard.project.ProjectService;
import io.github.hefajstosu.taskboard.task.TaskRequest;
import io.github.hefajstosu.taskboard.task.TaskService;
import io.github.hefajstosu.taskboard.user.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory; 
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class BoardQueryCountTest { 
    @Autowired
    UserService userService;
    @Autowired
    ProjectService projectService;
    @Autowired
    BoardColumnService columnService;
    @Autowired
    TaskService taskService;
    @Autowired
    BoardService boardService;
    @Autowired
    EntityManager entityManager;
    @Autowired
    EntityManagerFactory entityManagerFactory;

    @Test
    void boardIsLoadedWithConstantNumberOfQueries() {
        Long userId = userService.register("board@example.com", "Board", "password123").getId();
        Long projectId = projectService.create(new ProjectRequest("Big board", null), userId).id();
        for (int c = 0; c < 5; c++) {
            Long columnId = columnService.create(projectId, new ColumnRequest("col " + c, null), userId).id();
            for (int t = 0; t < 4; t++) {
                taskService.create(columnId, new TaskRequest("task " + c + "/" + t, null, null, userId, null), userId);
            }
        }
        entityManager.flush();
        entityManager.clear();

        Statistics stats = entityManagerFactory.unwrap(SessionFactory.class).getStatistics(); 
        stats.clear();

        BoardResponse board = boardService.getBoard(projectId, userId);

        assertThat(board.columns()).hasSize(5);
        assertThat(board.columns()).allSatisfy(column -> assertThat(column.tasks()).hasSize(4));
        // was 9
        assertThat(stats.getPrepareStatementCount()).isLessThanOrEqualTo(4);
    }
}
