package io.github.hefajstosu.taskboard.project;

import jakarta.persistence.*;

import java.time.Instant;

@Entity 
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    protected Project() {
    }

    public Project(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @PrePersist
    void prePersist(){
        createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }
    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getCreatedAt(){
        return createdAt;
    }
}
