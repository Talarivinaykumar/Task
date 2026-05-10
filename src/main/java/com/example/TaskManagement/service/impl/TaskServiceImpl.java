package com.example.TaskManagement.service.impl;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.TaskManagement.dto.PageResponse;
import com.example.TaskManagement.dto.TaskRequestDto;
import com.example.TaskManagement.dto.TaskResponseDto;
import com.example.TaskManagement.exception.InvalidTaskException;
import com.example.TaskManagement.exception.TaskNotFoundException;
import com.example.TaskManagement.mapper.TaskMapper;
import com.example.TaskManagement.model.Task;
import com.example.TaskManagement.model.TaskStatus;
import com.example.TaskManagement.repository.TaskRepository;
import com.example.TaskManagement.service.TaskService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {

	private final TaskRepository taskRepository;
	private final TaskMapper taskMapper;

	@Override
	public PageResponse<TaskResponseDto> findAll(int page, int size, TaskStatus status) {
		List<Task> filtered = taskRepository.findAll().stream()
				.filter(task -> status == null || task.getStatus() == status)
				.sorted(Comparator.comparing(Task::getId))
				.collect(Collectors.toList());

		long totalElements = filtered.size();
		int totalPages = size <= 0 ? 0 : (int) Math.ceil((double) totalElements / (double) size);
		int fromIndex = Math.min(page * size, filtered.size());
		int toIndex = Math.min(fromIndex + size, filtered.size());
		List<TaskResponseDto> slice = filtered.subList(fromIndex, toIndex).stream()
				.map(taskMapper::toResponse)
				.collect(Collectors.toList());

		log.debug("Listed tasks page={} size={} statusFilter={} total={}", page, size, status, totalElements);
		return PageResponse.<TaskResponseDto>builder()
				.content(slice)
				.page(page)
				.size(size)
				.totalElements(totalElements)
				.totalPages(totalPages)
				.build();
	}

	@Override
	public TaskResponseDto getById(Long id) {
		Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
		return taskMapper.toResponse(task);
	}

	@Override
	public TaskResponseDto create(TaskRequestDto dto) {
		TaskStatus status = dto.getStatus() == null ? TaskStatus.PENDING : dto.getStatus();
		LocalDateTime now = LocalDateTime.now();
		Task task = taskMapper.newEntityFromCreateRequest(dto, status);
		task.setCreatedAt(now);
		task.setUpdatedAt(now);
		Task saved = taskRepository.save(task);
		log.info("Created task id={} title='{}' status={}", saved.getId(), saved.getTitle(), saved.getStatus());
		return taskMapper.toResponse(saved);
	}

	@Override
	public TaskResponseDto update(Long id, TaskRequestDto dto) {
		Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
		taskMapper.apply(dto, task);
		task.setUpdatedAt(LocalDateTime.now());
		Task saved = taskRepository.save(task);
		log.info("Updated task id={} status={}", saved.getId(), saved.getStatus());
		return taskMapper.toResponse(saved);
	}

	@Override
	public void delete(Long id) {
		if (taskRepository.findById(id).isEmpty()) {
			throw new TaskNotFoundException(id);
		}
		taskRepository.deleteById(id);
		log.info("Deleted task id={}", id);
	}

	@Override
	public TaskResponseDto complete(Long id) {
		Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
		if (task.getStatus() == TaskStatus.COMPLETED) {
			throw new InvalidTaskException("Task is already completed");
		}
		task.setStatus(TaskStatus.COMPLETED);
		task.setUpdatedAt(LocalDateTime.now());
		Task saved = taskRepository.save(task);
		log.info("Marked task id={} as COMPLETED", saved.getId());
		return taskMapper.toResponse(saved);
	}
}
