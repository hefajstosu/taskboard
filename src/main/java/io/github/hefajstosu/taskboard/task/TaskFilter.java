package io.github.hefajstosu.taskboard.task; 

import java.time.LocalDate;

// from query params
public record TaskFilter(Long assigneeId, Priority priority, String q, LocalDate dueBefore){
} 
