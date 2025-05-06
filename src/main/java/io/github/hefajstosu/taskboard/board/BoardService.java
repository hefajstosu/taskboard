package io.github.hefajstosu.taskboard.board;

import io.github.hefajstosu.taskboard.project.Project;
import io.github.hefajstosu.taskboard.project.ProjectAccess;
import io.github.hefajstosu.taskboard.task.TaskRepository;
import io.github.hefajstosu.taskboard.task.TaskResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) 
public class BoardService {

    private final BoardColumnRepository columnRepository;
    private final TaskRepository taskRepository;
    private final ProjectAccess access;

    public BoardService(BoardColumnRepository columnRepository, TaskRepository taskRepository, ProjectAccess access) {
        this.columnRepository = columnRepository;
        this.taskRepository = taskRepository;
        this.access = access;
    }

    public BoardResponse getBoard(Long projectId, Long userId) {
        Project project = access.requireMember(projectId, userId).getProject();
        List<BoardResponse.Column> columns = columnRepository.findByProjectIdOrderByPosition(projectId).stream()
                .map(column -> new BoardResponse.Column(
                        column.getId(),
                        column.getName(),
                        column.getPosition(),
                        column.getWipLimit(),
                        taskRepository.findByColumnIdOrderByPosition(column.getId()).stream()
                                .map(TaskResponse::from)
                                .toList()))
                .toList();
        return new BoardResponse(project.getId(), project.getName(), columns);
    }
}
