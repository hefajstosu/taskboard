package io.github.hefajstosu.taskboard.board;

import io.github.hefajstosu.taskboard.project.Project;
import io.github.hefajstosu.taskboard.project.ProjectService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.github.hefajstosu.taskboard.common.NotFoundException;

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
        BoardColumn column = new BoardColumn(project, request.name(), position);
        column.setWipLimit(request.wipLimit());
        return ColumnResponse.from(columnRepository.save(column));
    }

    public ColumnResponse update(Long projectId, Long columnId, ColumnRequest request) {
		BoardColumn column = getColumn(projectId, columnId);
        column.setName(request.name());
        column.setWipLimit(request.wipLimit());
        return ColumnResponse.from(column);
    }

    public void delete(Long projectId, Long columnId) {
        columnRepository.delete(getColumn(projectId, columnId));
    }

    private BoardColumn getColumn(Long projectId, Long columnId) {
        BoardColumn column = getColumn(columnId);
        if (!column.getProject().getId().equals(projectId)){
            throw new NotFoundException("Column", columnId);
        }
        return column;
    }
    public BoardColumn getColumn(Long id){
        return columnRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Column", id));
    }
}
