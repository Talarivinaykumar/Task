package com.example.TaskManagement.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.TaskManagement.model.TaskStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDto {

	private Long id;
	private String title;
	private String description;
	private LocalDate dueDate;
	private TaskStatus status;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
