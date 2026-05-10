package com.example.TaskManagement.service;

import com.example.TaskManagement.dto.PageResponse;
import com.example.TaskManagement.dto.TaskRequestDto;
import com.example.TaskManagement.dto.TaskResponseDto;
import com.example.TaskManagement.model.TaskStatus;

public interface TaskService {

	PageResponse<TaskResponseDto> findAll(int page, int size, TaskStatus status);

	TaskResponseDto getById(Long id);

	TaskResponseDto create(TaskRequestDto dto);

	TaskResponseDto update(Long id, TaskRequestDto dto);

	void delete(Long id);

	TaskResponseDto complete(Long id);
}
