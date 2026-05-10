package com.example.TaskManagement.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Domain entity persisted in the in-memory repository.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {

	private Long id;
	private String title;
	private String description;
	private LocalDate dueDate;
	private TaskStatus status;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
