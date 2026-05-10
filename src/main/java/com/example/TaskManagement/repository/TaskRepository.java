package com.example.TaskManagement.repository;

import java.util.List;
import java.util.Optional;

import com.example.TaskManagement.model.Task;

public interface TaskRepository {

	Task save(Task task);

	Optional<Task> findById(Long id);

	void deleteById(Long id);

	List<Task> findAll();
}
