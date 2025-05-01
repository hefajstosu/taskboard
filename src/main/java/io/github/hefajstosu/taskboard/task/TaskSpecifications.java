package io.github.hefajstosu.taskboard.task;

import org.springframework.data.jpa.domain.Specification;

import java.util.Locale; 

final class TaskSpecifications {

    private TaskSpecifications() {
    }

    static Specification<Task> matching(Long projectId, TaskFilter filter) {
        Specification<Task> spec = inProject(projectId);
        if (filter.assigneeId() != null){
            spec = spec.and((root, query, cb) -> cb.equal(root.get("assignee").get("id"), filter.assigneeId()));
        }
        if (filter.priority() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("priority"), filter.priority()));
        }
        if (filter.q() != null && !filter.q().isBlank()) {
            String pattern = "%" + filter.q().trim().toLowerCase(Locale.ROOT) + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("title")), pattern));
        }
        if (filter.dueBefore() != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("dueDate"), filter.dueBefore()));
        }
        return spec;
    }

    private static Specification<Task> inProject(Long projectId) {
        return (root, query, cb) -> cb.equal(root.get("column").get("project").get("id"), projectId);
    }
}
