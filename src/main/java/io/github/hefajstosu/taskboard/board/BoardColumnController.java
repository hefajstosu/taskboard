package io.github.hefajstosu.taskboard.board;

import io.github.hefajstosu.taskboard.security.CurrentUserId;
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
    public List<ColumnResponse> list(@PathVariable Long projectId, @CurrentUserId Long userId) {
        return columnService.findByProject(projectId, userId);
    } 
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ColumnResponse create(@PathVariable Long projectId, @Valid @RequestBody ColumnRequest request,
                                 @CurrentUserId Long userId) {
        return columnService.create(projectId, request, userId);
    } 

    @PutMapping("/{columnId}")
    public ColumnResponse update(@PathVariable Long projectId, @PathVariable Long columnId,
                                 @Valid @RequestBody ColumnRequest request, @CurrentUserId Long userId) {
        return columnService.update(projectId, columnId, request, userId);
    } 

    @DeleteMapping("/{columnId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long projectId, @PathVariable Long columnId, @CurrentUserId Long userId){
        columnService.delete(projectId, columnId, userId);
    } 
} 
