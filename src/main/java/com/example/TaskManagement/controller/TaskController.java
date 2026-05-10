package com.example.TaskManagement.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.TaskManagement.dto.PageResponse;
import com.example.TaskManagement.dto.TaskRequestDto;
import com.example.TaskManagement.dto.TaskResponseDto;
import com.example.TaskManagement.model.TaskStatus;
import com.example.TaskManagement.service.TaskService;
import com.example.TaskManagement.validation.ValidationGroups.OnCreate;
import com.example.TaskManagement.validation.ValidationGroups.OnUpdate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tasks")
@Validated
@RequiredArgsConstructor
public class TaskController {

	private final TaskService taskService;

	@GetMapping
	public ResponseEntity<PageResponse<TaskResponseDto>> listTasks(
			@RequestParam(defaultValue = "0") @Min(0) int page,
			@RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
			@RequestParam(required = false) TaskStatus status) {
		return ResponseEntity.ok(taskService.findAll(page, size, status));
	}

	@GetMapping("/{id}")
	public ResponseEntity<TaskResponseDto> getTask(@PathVariable Long id) {
		return ResponseEntity.ok(taskService.getById(id));
	}

	@PostMapping
	public ResponseEntity<TaskResponseDto> createTask(@Validated(OnCreate.class) @RequestBody TaskRequestDto request) {
		TaskResponseDto created = taskService.create(request);
		URI location = URI.create("/tasks/" + created.getId());
		return ResponseEntity.created(location).body(created);
	}

	@PutMapping("/{id}")
	public ResponseEntity<TaskResponseDto> updateTask(@PathVariable Long id,
			@Validated(OnUpdate.class) @RequestBody TaskRequestDto request) {
		return ResponseEntity.ok(taskService.update(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
		taskService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/{id}/complete")
	public ResponseEntity<TaskResponseDto> completeTask(@PathVariable Long id) {
		return ResponseEntity.ok(taskService.complete(id));
	}
}
