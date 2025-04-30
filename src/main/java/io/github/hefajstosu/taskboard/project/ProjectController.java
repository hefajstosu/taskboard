package io.github.hefajstosu.taskboard.project;

import io.github.hefajstosu.taskboard.security.CurrentUserId;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {


    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService; 
    }

    @GetMapping
    public List<ProjectResponse> list(@CurrentUserId Long userId) {
        return projectService.findAll(userId);
    }

    @GetMapping("/{id}")
    public ProjectResponse get(@PathVariable Long id, @CurrentUserId Long userId) {
		return projectService.findById(id, userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse create(@Valid @RequestBody ProjectRequest request, @CurrentUserId Long userId){
        return projectService.create(request, userId);
    }

    @PutMapping("/{id}")
    public ProjectResponse update(@PathVariable Long id, @Valid @RequestBody ProjectRequest request, 
                                  @CurrentUserId Long userId) {
        return projectService.update(id, request, userId);
    }

    @DeleteMapping("/{id}") 
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, @CurrentUserId Long userId) { 
        projectService.delete(id, userId);
    }

    @GetMapping("/{id}/members")
    public List<MemberResponse> members(@PathVariable Long id, @CurrentUserId Long userId) {
        return projectService.members(id, userId);
    }

    @PostMapping("/{id}/members")
    @ResponseStatus(HttpStatus.CREATED)
    public MemberResponse addMember(@PathVariable Long id, @Valid @RequestBody AddMemberRequest request,
                                    @CurrentUserId Long userId) {
        return projectService.addMember(id, request, userId);
    }


    @DeleteMapping("/{id}/members/{memberUserId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMember(@PathVariable Long id, @PathVariable Long memberUserId, @CurrentUserId Long userId) {
        projectService.removeMember(id, memberUserId, userId);
    }
}
