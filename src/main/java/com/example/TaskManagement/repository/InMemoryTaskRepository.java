package com.example.TaskManagement.repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.TaskManagement.model.Task;
/**
 * Thread-safe in-memory persistence suitable for demos and tests.
 */
@Repository
public class InMemoryTaskRepository implements TaskRepository {

	private final ConcurrentHashMap<Long, Task> store = new ConcurrentHashMap<>();
	private final AtomicLong idSequence = new AtomicLong(1);

	@Override
	public Task save(Task task) {
		if (task.getId() == null) {
			task.setId(idSequence.getAndIncrement());
		}
		store.put(task.getId(), task);
		return task;
	}

	@Override
	public Optional<Task> findById(Long id) {
		return Optional.ofNullable(store.get(id));
	}

	@Override
	public void deleteById(Long id) {
		store.remove(id);
	}

	@Override
	public List<Task> findAll() {
		return store.values().stream()
				.sorted(Comparator.comparing(Task::getId))
				.collect(Collectors.toCollection(ArrayList::new));
	}

}
