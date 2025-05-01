package io.github.hefajstosu.taskboard.comment;

import io.github.hefajstosu.taskboard.security.CurrentUserId;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api") 
public class CommentController {
    private final CommentService commentService;
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/tasks/{taskId}/comments")
    public List<CommentResponse> list(@PathVariable Long taskId, @CurrentUserId Long userId) {
        return commentService.findByTask(taskId, userId);
    }
    @PostMapping("/tasks/{taskId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse add(@PathVariable Long taskId, @Valid @RequestBody CommentRequest request,
                               @CurrentUserId Long userId) {
        return commentService.add(taskId, request, userId);
    }


    @DeleteMapping("/comments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, @CurrentUserId Long userId) {
        commentService.delete(id, userId);
    }
}
