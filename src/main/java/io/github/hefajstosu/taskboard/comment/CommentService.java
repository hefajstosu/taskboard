package io.github.hefajstosu.taskboard.comment;

import io.github.hefajstosu.taskboard.common.NotFoundException;
import io.github.hefajstosu.taskboard.project.ProjectAccess;
import io.github.hefajstosu.taskboard.project.ProjectMember;
import io.github.hefajstosu.taskboard.task.Task;
import io.github.hefajstosu.taskboard.task.TaskService;
import io.github.hefajstosu.taskboard.user.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskService taskService;
    private final UserService userService;
    private final ProjectAccess access;
    public CommentService(CommentRepository commentRepository, TaskService taskService,
                          UserService userService, ProjectAccess access) {
        this.commentRepository = commentRepository;
        this.taskService = taskService;
        this.userService = userService;
        this.access = access;
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> findByTask(Long taskId, Long userId){
        taskService.getTask(taskId, userId); 
        return commentRepository.findByTaskIdOrderByCreatedAt(taskId).stream()
                .map(CommentResponse::from)
            .toList();
    }

    public CommentResponse add(Long taskId, CommentRequest request, Long userId) {
        Task task = taskService.getTask(taskId, userId);
        Comment comment = new Comment(task, userService.getUser(userId), request.body());
        return CommentResponse.from(commentRepository.save(comment));
    }

    // author or owner
    public void delete(Long commentId, Long userId){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment", commentId));
        Long projectId = comment.getTask().getColumn().getProject().getId();
        ProjectMember member = access.requireMember(projectId, userId);


        boolean isAuthor = comment.getAuthor().getId().equals(userId);
		if (!isAuthor && !member.isOwner()){
            throw new AccessDeniedException("You can only delete your own comments");
        }
        commentRepository.delete(comment); 
    }
}
