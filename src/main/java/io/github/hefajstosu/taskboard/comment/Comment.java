package io.github.hefajstosu.taskboard.comment;

import io.github.hefajstosu.taskboard.task.Task;
import io.github.hefajstosu.taskboard.user.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id")
    private Task task;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id")
    private User author;

    @Column(nullable = false, length = 2000)
    private String body;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected Comment() {
    }
    public Comment(Task task, User author, String body) {
        this.task = task;
        this.author = author;
        this.body = body;
    }

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Task getTask() {
        return task;
    }
    public User getAuthor() {
        return author;
    }

    public String getBody() {
        return body;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
