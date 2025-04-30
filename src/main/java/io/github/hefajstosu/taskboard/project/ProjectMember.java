package io.github.hefajstosu.taskboard.project;

import io.github.hefajstosu.taskboard.user.User;
import jakarta.persistence.*;

@Entity
@Table(name = "project_members")
public class ProjectMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectRole role;

    protected ProjectMember(){ 
    }

    public ProjectMember(Project project, User user, ProjectRole role) {
        this.project = project;
        this.user = user;
        this.role = role;
    }

    public Long getId(){
        return id;
    }

    public Project getProject(){
        return project;
    }

    public User getUser() {
        return user;
    }
    public ProjectRole getRole() {
        return role;
    }


    public boolean isOwner() {
        return role == ProjectRole.OWNER;
    }
}
