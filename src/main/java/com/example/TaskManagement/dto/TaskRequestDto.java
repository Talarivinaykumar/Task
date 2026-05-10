package com.example.TaskManagement.dto;

import java.time.LocalDate;

import com.example.TaskManagement.model.TaskStatus;
import com.example.TaskManagement.validation.ValidationGroups.OnCreate;
import com.example.TaskManagement.validation.ValidationGroups.OnUpdate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequestDto {

	@NotBlank(groups = { OnCreate.class, OnUpdate.class }, message = "Title is required")
	@Size(max = 200, message = "Title must be at most 200 characters")
	private String title;

	@Size(max = 2000, message = "Description must be at most 2000 characters")
	private String description;

	private LocalDate dueDate;

	/**
	 * Optional on create (defaults to {@link TaskStatus#PENDING}); required on full update.
	 */
	@NotNull(groups = OnUpdate.class, message = "Status is required for update")
	private TaskStatus status;
}
