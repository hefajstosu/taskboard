package io.github.hefajstosu.taskboard.project;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectRepository projectRepository;

    public ProjectController(ProjectRepository projectRepository) { 
        this.projectRepository = projectRepository;
    }

    @GetMapping
    public List<Project> list() {
        return projectRepository.findAll();
    }

    @GetMapping("/{id}")
    public Project get(@PathVariable Long id) {
        return projectRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Project create(@RequestBody Project project) {
        return projectRepository.save(project);
    }

    @PutMapping("/{id}")
    public Project update(@PathVariable Long id, @RequestBody Project body) {
        Project project = get(id);
        project.setName(body.getName());
        project.setDescription(body.getDescription()); 
        return projectRepository.save(project);
    }

    @DeleteMapping("/{id}") 
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        projectRepository.deleteById(id);
    }
}
