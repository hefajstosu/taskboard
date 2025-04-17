package io.github.hefajstosu.taskboard.task;

import io.github.hefajstosu.taskboard.board.BoardColumn;
import io.github.hefajstosu.taskboard.board.BoardColumnService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.github.hefajstosu.taskboard.common.BusinessException;
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

    // TODO cleanup
    public TaskResponse move(Long id, MoveTaskRequest request){
        Task task = getTask(id);
        BoardColumn source = task.getColumn();
		BoardColumn target = columnService.getColumn(request.columnId());

        if(!source.getProject().getId().equals(target.getProject().getId())) {
            throw new BusinessException("Cannot move task to a column of another project"); 
        }

        int from = task.getPosition();

        if (source.getId().equals(target.getId())) {
            List<Task> tasks = taskRepository.findByColumnIdOrderByPosition(source.getId());
            int to = Math.min(request.position(), tasks.size() - 1);
            for (Task other : tasks) {
                int p = other.getPosition();
                if(to > from && p > from && p < to) {
                    other.setPosition(p - 1);
                } else if (to < from && p >= to && p < from) {
                    other.setPosition(p + 1);
                }
            }
            task.setPosition(to);
		} else {
            List<Task> sourceTasks = taskRepository.findByColumnIdOrderByPosition(source.getId());
            List<Task> targetTasks = taskRepository.findByColumnIdOrderByPosition(target.getId());
            int to = Math.min(request.position(), targetTasks.size());
            sourceTasks.stream()
                    .filter(t -> t.getPosition() > from)
                    .forEach(t -> t.setPosition(t.getPosition() - 1));
            targetTasks.stream()
                    .filter(t -> t.getPosition() >= to)
                        .forEach(t -> t.setPosition(t.getPosition() + 1));
            task.setColumn(target);
            task.setPosition(to);
        }
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
