package io.github.hefajstosu.taskboard.project;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.github.hefajstosu.taskboard.common.NotFoundException;

import java.util.List; 

@Service
@Transactional
public class ProjectService {


    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> findAll() {
        return projectRepository.findAll().stream()
            .map(ProjectResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse findById(Long id) {
        return ProjectResponse.from(getProject(id));
    }
    public ProjectResponse create(ProjectRequest request) {
        Project project = projectRepository.save(new Project(request.name(), request.description()));
        return ProjectResponse.from(project);
    }


    public ProjectResponse update(Long id, ProjectRequest request){
        Project project = getProject(id);
        project.setName(request.name());
        project.setDescription(request.description());
        return ProjectResponse.from(project);
    }

    public void delete(Long id){
        projectRepository.delete(getProject(id));
    }

    public Project getProject(Long id){
        return projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Project", id));
    }
}
