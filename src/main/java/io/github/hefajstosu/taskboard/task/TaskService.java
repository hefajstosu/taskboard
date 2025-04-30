package io.github.hefajstosu.taskboard.task;

import io.github.hefajstosu.taskboard.board.BoardColumn;
import io.github.hefajstosu.taskboard.board.BoardColumnService;
import io.github.hefajstosu.taskboard.board.WipCheck; 
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.github.hefajstosu.taskboard.common.BusinessException;
import io.github.hefajstosu.taskboard.common.NotFoundException;
import io.github.hefajstosu.taskboard.project.ProjectAccess;

import java.util.List;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final BoardColumnService columnService;
    private final ProjectAccess access;

    public TaskService(TaskRepository taskRepository, BoardColumnService columnService, ProjectAccess access) {
        this.taskRepository = taskRepository;
        this.columnService = columnService;
        this.access = access;
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> findByColumn(Long columnId, Long userId) { 
        requireMember(columnService.getColumn(columnId), userId);
        return taskRepository.findByColumnIdOrderByPosition(columnId).stream()
            .map(TaskResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse findById(Long id, Long userId) {
        return TaskResponse.from(getTask(id, userId));
    }

    public TaskResponse create(Long columnId, TaskRequest request, Long userId) {
        BoardColumn column = columnService.getColumn(columnId);
        requireMember(column, userId);
        int count = taskRepository.countByColumnId(columnId);
        ensureWipAllows(column, count);
        Task task = new Task(column, request.title(), count);
        applyChanges(task, request);
        return TaskResponse.from(taskRepository.save(task));
    }

    public TaskResponse update(Long id, TaskRequest request, Long userId) {
        Task task = getTask(id, userId); 
        task.setTitle(request.title());
        applyChanges(task, request);
        return TaskResponse.from(task);
    }

    // TODO cleanup
    public TaskResponse move(Long id, MoveTaskRequest request, Long userId) {
        Task task = getTask(id, userId); 
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
                if (to > from && p > from && p <= to) {
                    other.setPosition(p - 1);
                } else if (to < from && p >= to && p < from) {
                    other.setPosition(p + 1);
                }
            }
            task.setPosition(to);
		} else {
            List<Task> sourceTasks = taskRepository.findByColumnIdOrderByPosition(source.getId());
            List<Task> targetTasks = taskRepository.findByColumnIdOrderByPosition(target.getId());
            ensureWipAllows(target, targetTasks.size());
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

    public void delete(Long id, Long userId) { 
        taskRepository.delete(getTask(id, userId));
    }

    private void ensureWipAllows(BoardColumn column, int currentTaskCount) {
        switch(WipCheck.of(column, currentTaskCount)) {
            case WipCheck.Allowed() -> {
            }
            case WipCheck.LimitReached(String name, int limit) ->
                    throw new BusinessException("Column '%s' reached its WIP limit of %d".formatted(name, limit));
        }
    }

    private void applyChanges(Task task, TaskRequest request) {
        task.setDescription(request.description());
        if(request.priority() != null) {
            task.setPriority(request.priority());
        }
    }

    private Task getTask(Long id, Long userId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task", id));
        requireMember(task.getColumn(), userId);
        return task;
    }

    private void requireMember(BoardColumn column, Long userId){
        access.requireMember(column.getProject().getId(), userId);
    }
}
