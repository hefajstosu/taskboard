package io.github.hefajstosu.taskboard.task;

import io.github.hefajstosu.taskboard.security.CurrentUserId;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService){
        this.taskService = taskService;
    }

    @GetMapping("/columns/{columnId}/tasks")
    public List<TaskResponse> list(@PathVariable Long columnId, @CurrentUserId Long userId) {
        return taskService.findByColumn(columnId, userId);
    }

    @PostMapping("/columns/{columnId}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(@PathVariable Long columnId, @Valid @RequestBody TaskRequest request,
                               @CurrentUserId Long userId) {
        return taskService.create(columnId, request, userId);
    }

    @GetMapping("/tasks/{id}")
    public TaskResponse get(@PathVariable Long id, @CurrentUserId Long userId) {
        return taskService.findById(id, userId);
    }

    @PutMapping("/tasks/{id}")
    public TaskResponse update(@PathVariable Long id, @Valid @RequestBody TaskRequest request,
                               @CurrentUserId Long userId) {
        return taskService.update(id, request, userId);
    }


    @PatchMapping("/tasks/{id}/move")
    public TaskResponse move(@PathVariable Long id, @Valid @RequestBody MoveTaskRequest request,
                             @CurrentUserId Long userId) {
        return taskService.move(id, request, userId);
    }

    @DeleteMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, @CurrentUserId Long userId) {
        taskService.delete(id, userId);
    }
}
