package io.github.hefajstosu.taskboard.board;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.hefajstosu.taskboard.project.Project;
import jakarta.persistence.*;

@Entity
@Table(name = "board_columns")
public class BoardColumn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id")
    private Project project;


    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int position;

    protected BoardColumn() {
    }

    public BoardColumn(Project project, String name, int position) {
        this.project = project;
        this.name = name;
        this.position = position;
    }

    public Long getId(){
        return id;
    }

    public Project getProject(){
        return project;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) { 
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
