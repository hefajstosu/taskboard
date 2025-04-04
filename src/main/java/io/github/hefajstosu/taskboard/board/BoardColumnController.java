package io.github.hefajstosu.taskboard.board;

import io.github.hefajstosu.taskboard.project.Project;
import io.github.hefajstosu.taskboard.project.ProjectRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List; 

@RestController
@RequestMapping("/api/projects/{projectId}/columns")
public class BoardColumnController { 

    private final BoardColumnRepository columnRepository;
    private final ProjectRepository projectRepository;

    public BoardColumnController(BoardColumnRepository columnRepository, ProjectRepository projectRepository){
        this.columnRepository = columnRepository;
        this.projectRepository = projectRepository;
    } 

    @GetMapping
    public List<BoardColumn> list(@PathVariable Long projectId) {
        return columnRepository.findByProjectIdOrderByPosition(projectId);
    } 


    record CreateColumn(String name) {
    } 
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BoardColumn create(@PathVariable Long projectId, @RequestBody CreateColumn body) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        int position = columnRepository.countByProjectId(projectId);
        return columnRepository.save(new BoardColumn(project, body.name(), position));
    } 

    @DeleteMapping("/{columnId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long projectId, @PathVariable Long columnId) {
        columnRepository.deleteById(columnId);
    } 
} 
