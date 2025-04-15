package io.github.hefajstosu.taskboard.task;

import io.github.hefajstosu.taskboard.board.BoardColumn;
import io.github.hefajstosu.taskboard.board.BoardColumnService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.github.hefajstosu.taskboard.common.NotFoundException;

import java.util.List;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final BoardColumnService columnService;

    public TaskService(TaskRepository taskRepository, BoardColumnService columnService) {
        this.taskRepository = taskRepository;
        this.columnService = columnService;
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> findByColumn(Long columnId) {
        return taskRepository.findByColumnIdOrderByPosition(columnId).stream()
            .map(TaskResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse findById(Long id) {
        return TaskResponse.from(getTask(id)); 
    }

    public TaskResponse create(Long columnId, TaskRequest request) {
        BoardColumn column = columnService.getColumn(columnId);
        Task task = new Task(column, request.title(), taskRepository.countByColumnId(columnId));
        applyChanges(task, request);
        return TaskResponse.from(taskRepository.save(task));
    }

    public TaskResponse update(Long id, TaskRequest request) {
        Task task = getTask(id);
        task.setTitle(request.title());
        applyChanges(task, request);
        return TaskResponse.from(task);
    }


    public void delete(Long id) {
        taskRepository.delete(getTask(id));
    }

    private void applyChanges(Task task, TaskRequest request) {
        task.setDescription(request.description());
        if(request.priority() != null) {
            task.setPriority(request.priority());
        }
    }
    private Task getTask(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task", id));
    }
}
