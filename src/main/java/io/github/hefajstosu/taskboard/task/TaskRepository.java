package io.github.hefajstosu.taskboard.task;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByColumnIdOrderByPosition(Long columnId);

    int countByColumnId(Long columnId);
}
