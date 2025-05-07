package io.github.hefajstosu.taskboard.task;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    List<Task> findByColumnIdOrderByPosition(Long columnId);

    int countByColumnId(Long columnId);

    // fetch assignee too
    @EntityGraph(attributePaths = "assignee")
    List<Task> findByColumnProjectIdOrderByPosition(Long projectId);
}
