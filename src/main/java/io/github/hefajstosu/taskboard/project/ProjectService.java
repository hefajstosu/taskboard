package io.github.hefajstosu.taskboard.project;

import io.github.hefajstosu.taskboard.common.BusinessException;
import io.github.hefajstosu.taskboard.common.NotFoundException;
import io.github.hefajstosu.taskboard.user.User;
import io.github.hefajstosu.taskboard.user.UserRepository;
import io.github.hefajstosu.taskboard.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List; 

@Service
@Transactional
public class ProjectService {


    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository memberRepository;
    private final ProjectAccess access;
    private final UserService userService;
    private final UserRepository userRepository;


    public ProjectService(ProjectRepository projectRepository,
                          ProjectMemberRepository memberRepository,
                          ProjectAccess access,
                          UserService userService,
                          UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.memberRepository = memberRepository;
        this.access = access;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> findAll(Long userId) {
		return projectRepository.findAllForUser(userId).stream() 
            .map(ProjectResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse findById(Long id, Long userId) {
        return ProjectResponse.from(access.requireMember(id, userId).getProject());
    }

    public ProjectResponse create(ProjectRequest request, Long userId) {
        User owner = userService.getUser(userId);
        Project project = projectRepository.save(new Project(request.name(), request.description()));
        memberRepository.save(new ProjectMember(project, owner, ProjectRole.OWNER));
        return ProjectResponse.from(project);
    }

    public ProjectResponse update(Long id, ProjectRequest request, Long userId) {
        Project project = access.requireMember(id, userId).getProject();
        project.setName(request.name());
        project.setDescription(request.description());
        return ProjectResponse.from(project);
    }

    public void delete(Long id, Long userId) {
        Project project = access.requireOwner(id, userId).getProject();
        projectRepository.delete(project);
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> members(Long projectId, Long userId) {
		access.requireMember(projectId, userId);
        return memberRepository.findByProjectId(projectId).stream()
                .map(MemberResponse::from)
                .toList();
    }

    public MemberResponse addMember(Long projectId, AddMemberRequest request, Long userId) {
        Project project = access.requireOwner(projectId, userId).getProject();
        User user = userRepository.findByEmailIgnoreCase(request.email().trim())
                .orElseThrow(() -> new NotFoundException("User", request.email()));
        if (memberRepository.existsByProjectIdAndUserId(projectId, user.getId())){
            throw new BusinessException("User is already a member of this project"); 
        }
        return MemberResponse.from(memberRepository.save(new ProjectMember(project, user, ProjectRole.MEMBER)));
    }

    public void removeMember(Long projectId, Long memberUserId, Long userId) {
        access.requireOwner(projectId, userId);
        ProjectMember member = memberRepository.findByProjectIdAndUserId(projectId, memberUserId)
                .orElseThrow(() -> new NotFoundException("Member", memberUserId));
        if(member.isOwner()) {
            throw new BusinessException("Owner cannot be removed from the project");
        }
        memberRepository.delete(member);
    }

    public Project getProject(Long id){
        return projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Project", id));
    }
}
