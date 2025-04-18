package io.github.hefajstosu.taskboard.task;

import io.github.hefajstosu.taskboard.board.BoardColumn;
import io.github.hefajstosu.taskboard.board.BoardColumnService;
import io.github.hefajstosu.taskboard.common.BusinessException;
import io.github.hefajstosu.taskboard.project.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock 
    TaskRepository taskRepository;

    @Mock 
    BoardColumnService columnService;

    @InjectMocks
    TaskService taskService;

    Project project;
    BoardColumn todo;
    BoardColumn done;
    List<Task> todoTasks;
    List<Task> doneTasks;

    @BeforeEach
    void setUp() {
        project = withId(new Project("Demo", null), 1L);
        todo = withId(new BoardColumn(project, "To do", 0), 10L);
        done = withId(new BoardColumn(project, "Done", 1), 11L);

        todoTasks = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            todoTasks.add(withId(new Task(todo, "todo-" + i, i), 100L + i));
        }
        doneTasks = new ArrayList<>();
        for(int i = 0; i < 2; i++) {
            doneTasks.add(withId(new Task(done, "done-" + i, i), 200L + i));
        }
        lenient().when(columnService.getColumn(10L)).thenReturn(todo);
        lenient().when(columnService.getColumn(11L)).thenReturn(done);
        lenient().when(taskRepository.findByColumnIdOrderByPosition(10L)).thenReturn(todoTasks);
        lenient().when(taskRepository.findByColumnIdOrderByPosition(11L)).thenReturn(doneTasks);
        todoTasks.forEach(t -> lenient().when(taskRepository.findById(t.getId())).thenReturn(Optional.of(t)));
    }
    @Test
    void movesTaskUpWithinColumn() {
        taskService.move(103L, new MoveTaskRequest(10L, 1));
        assertThat(titlesByPosition(todoTasks)).containsExactly("todo-0", "todo-3", "todo-1", "todo-2");
    }
    @Test
    void movesTaskDownWithinColumn() {
        taskService.move(100L, new MoveTaskRequest(10L, 2));

        assertThat(titlesByPosition(todoTasks)).containsExactly("todo-1", "todo-2", "todo-0", "todo-3");
    }
    @Test
    void clampsPositionToEndOfColumn() {
        taskService.move(101L, new MoveTaskRequest(10L, 99));

        assertThat(titlesByPosition(todoTasks)).containsExactly("todo-0", "todo-2", "todo-3", "todo-1");
    }
    @Test
    void movesTaskToAnotherColumn() {
        Task moved = todoTasks.get(1);

        taskService.move(101L, new MoveTaskRequest(11L, 1));


        todoTasks.remove(moved); 
        doneTasks.add(moved);
        assertThat(moved.getColumn()).isSameAs(done);
        assertThat(titlesByPosition(todoTasks)).containsExactly("todo-0", "todo-2", "todo-3");
        assertThat(titlesByPosition(doneTasks)).containsExactly("done-0", "todo-1", "done-1");
    }
    @Test
    void rejectsMoveToColumnOfOtherProject() {
        Project other = withId(new Project("Other", null), 2L);
        BoardColumn foreign = withId(new BoardColumn(other, "Backlog", 0), 50L);
        lenient().when(columnService.getColumn(50L)).thenReturn(foreign);

        assertThatThrownBy(() -> taskService.move(100L, new MoveTaskRequest(50L, 0)))
                .isInstanceOf(BusinessException.class);
    }
    @Test
    void respectsWipLimitOfTargetColumn() {
        done.setWipLimit(2); 


        assertThatThrownBy(() -> taskService.move(100L, new MoveTaskRequest(11L, 0)))
                .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("WIP limit of 2");
    }
    @Test
    void wipLimitDoesNotBlockReorderInSameColumn() {
        todo.setWipLimit(4);

        taskService.move(103L, new MoveTaskRequest(10L, 0));

        assertThat(titlesByPosition(todoTasks)).containsExactly("todo-3", "todo-0", "todo-1", "todo-2");
    }

    private static List<String> titlesByPosition(List<Task> tasks) {
        return tasks.stream() 
                .sorted((a, b) -> Integer.compare(a.getPosition(), b.getPosition()))
                    .map(Task::getTitle)
                .toList();
    }

    private static <T> T withId(T entity, Long id) {
        ReflectionTestUtils.setField(entity, "id", id);
        return entity;
    }
}
