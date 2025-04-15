package io.github.hefajstosu.taskboard.board;

import io.github.hefajstosu.taskboard.project.Project;
import io.github.hefajstosu.taskboard.project.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class BoardColumnService {

    private final BoardColumnRepository columnRepository;
    private final ProjectService projectService;

    public BoardColumnService(BoardColumnRepository columnRepository, ProjectService projectService){
        this.columnRepository = columnRepository;
        this.projectService = projectService;
    }

    @Transactional(readOnly = true)
    public List<ColumnResponse> findByProject(Long projectId) {
        return columnRepository.findByProjectIdOrderByPosition(projectId).stream()
                    .map(ColumnResponse::from)
            .toList();
    }

    public ColumnResponse create(Long projectId, ColumnRequest request) {
        Project project = projectService.getProject(projectId);
        int position = columnRepository.countByProjectId(projectId);
        return ColumnResponse.from(columnRepository.save(new BoardColumn(project, request.name(), position)));
    }

    public void delete(Long projectId, Long columnId) {
        BoardColumn column = getColumn(columnId);
        if (!column.getProject().getId().equals(projectId)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Column " + columnId + " not found");
        }
        columnRepository.delete(column);
    }
    public BoardColumn getColumn(Long id){
        return columnRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Column " + id + " not found"));
    }
}
