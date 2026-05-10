package com.example.TaskManagement.mapper;

import org.springframework.stereotype.Component;

import com.example.TaskManagement.dto.TaskRequestDto;
import com.example.TaskManagement.dto.TaskResponseDto;
import com.example.TaskManagement.model.Task;
import com.example.TaskManagement.model.TaskStatus;

@Component
public class TaskMapper {

	public TaskResponseDto toResponse(Task task) {
		if (task == null) {
			return null;
		}
		return TaskResponseDto.builder()
				.id(task.getId())
				.title(task.getTitle())
				.description(task.getDescription())
				.dueDate(task.getDueDate())
				.status(task.getStatus())
				.createdAt(task.getCreatedAt())
				.updatedAt(task.getUpdatedAt())
				.build();
	}

	/**
	 * Applies request fields to an existing entity (PUT semantics).
	 */
	public void apply(TaskRequestDto dto, Task entity) {
		entity.setTitle(dto.getTitle());
		entity.setDescription(dto.getDescription());
		entity.setDueDate(dto.getDueDate());
		entity.setStatus(dto.getStatus());
	}

	public Task newEntityFromCreateRequest(TaskRequestDto dto, TaskStatus resolvedStatus) {
		return Task.builder()
				.title(dto.getTitle())
				.description(dto.getDescription())
				.dueDate(dto.getDueDate())
				.status(resolvedStatus)
				.build();
	}
}
