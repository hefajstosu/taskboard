package io.github.hefajstosu.taskboard.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("""
            select m.project from ProjectMember m
            where m.user.id = :userId
            order by m.project.createdAt desc
            """)
    List<Project> findAllForUser(Long userId);
}
