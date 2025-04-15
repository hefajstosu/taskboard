package io.github.hefajstosu.taskboard.task;

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
    public List<TaskResponse> list(@PathVariable Long columnId) {
        return taskService.findByColumn(columnId);
    }

    @PostMapping("/columns/{columnId}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(@PathVariable Long columnId, @Valid @RequestBody TaskRequest request) { 
        return taskService.create(columnId, request);
    }

    @GetMapping("/tasks/{id}")
    public TaskResponse get(@PathVariable Long id) {
        return taskService.findById(id);
    }

    @PutMapping("/tasks/{id}")
    public TaskResponse update(@PathVariable Long id, @Valid @RequestBody TaskRequest request){
        return taskService.update(id, request);
    }

    @DeleteMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        taskService.delete(id);
    }
}
