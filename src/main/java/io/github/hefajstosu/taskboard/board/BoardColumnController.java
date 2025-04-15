package io.github.hefajstosu.taskboard.board;

import jakarta.validation.Valid; 
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List; 

@RestController
@RequestMapping("/api/projects/{projectId}/columns")
public class BoardColumnController { 
    private final BoardColumnService columnService;

    public BoardColumnController(BoardColumnService columnService) {
        this.columnService = columnService;
    } 

    @GetMapping
    public List<ColumnResponse> list(@PathVariable Long projectId) {
        return columnService.findByProject(projectId); 
    } 
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ColumnResponse create(@PathVariable Long projectId, @Valid @RequestBody ColumnRequest request) {
        return columnService.create(projectId, request);
    } 

    @DeleteMapping("/{columnId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long projectId, @PathVariable Long columnId) {
        columnService.delete(projectId, columnId);
    } 
} 
