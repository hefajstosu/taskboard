package io.github.hefajstosu.taskboard.task;

import io.github.hefajstosu.taskboard.board.BoardColumn;
import io.github.hefajstosu.taskboard.board.BoardColumnRepository; 
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TaskController {

    private final TaskRepository taskRepository;
    private final BoardColumnRepository columnRepository;
    public TaskController(TaskRepository taskRepository, BoardColumnRepository columnRepository) {
        this.taskRepository = taskRepository;
        this.columnRepository = columnRepository;
    }

    @GetMapping("/columns/{columnId}/tasks")
    public List<Task> list(@PathVariable Long columnId) {
        return taskRepository.findByColumnIdOrderByPosition(columnId);
    }

    record CreateTask(String title, String description, Priority priority){
    }

    @PostMapping("/columns/{columnId}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public Task create(@PathVariable Long columnId, @RequestBody CreateTask body) {
        BoardColumn column = columnRepository.findById(columnId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Task task = new Task(column, body.title(), taskRepository.countByColumnId(columnId));
        task.setDescription(body.description());
        if(body.priority() != null) { 
            task.setPriority(body.priority()); 
        }
        return taskRepository.save(task); 
    }

    @GetMapping("/tasks/{id}")
    public Task get(@PathVariable Long id) {
        return taskRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/tasks/{id}")
    public Task update(@PathVariable Long id, @RequestBody CreateTask body) {
        Task task = get(id);
		task.setTitle(body.title());
        task.setDescription(body.description());
        if(body.priority() != null) { 
            task.setPriority(body.priority()); 
        }
        return taskRepository.save(task); 
    }

    @DeleteMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        taskRepository.deleteById(id);
    }
}
