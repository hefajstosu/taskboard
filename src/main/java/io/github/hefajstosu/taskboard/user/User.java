package io.github.hefajstosu.taskboard.user;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "app_users") 
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String displayName;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected User() {
    }

    public User(String email, String displayName, String passwordHash) {
        this.email = email;
		this.displayName = displayName;
        this.passwordHash = passwordHash;
    }

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }
    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName; 
    }


    public String getPasswordHash() {
        return passwordHash;
    }

    public Instant getCreatedAt(){
        return createdAt;
    }
}
