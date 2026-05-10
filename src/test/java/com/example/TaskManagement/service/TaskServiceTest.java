package com.example.TaskManagement.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.TaskManagement.dto.TaskRequestDto;
import com.example.TaskManagement.dto.TaskResponseDto;
import com.example.TaskManagement.exception.InvalidTaskException;
import com.example.TaskManagement.exception.TaskNotFoundException;
import com.example.TaskManagement.mapper.TaskMapper;
import com.example.TaskManagement.model.Task;
import com.example.TaskManagement.model.TaskStatus;
import com.example.TaskManagement.repository.TaskRepository;
import com.example.TaskManagement.service.impl.TaskServiceImpl;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

	@Mock
	private TaskRepository taskRepository;

	private TaskMapper taskMapper;
	private TaskServiceImpl taskService;

	@BeforeEach
	void setUp() {
		taskMapper = new TaskMapper();
		taskService = new TaskServiceImpl(taskRepository, taskMapper);
	}

	@Test
	void create_shouldPersistWithDefaultStatusPendingWhenStatusOmitted() {
		TaskRequestDto dto = TaskRequestDto.builder()
				.title("Interview preparation")
				.description("System design and algorithms")
				.dueDate(LocalDate.of(2026, 5, 20))
				.build();

		when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
			Task task = invocation.getArgument(0);
			if (task.getId() == null) {
				task.setId(1L);
			}
			return task;
		});

		TaskResponseDto created = taskService.create(dto);

		assertThat(created.getId()).isEqualTo(1L);
		assertThat(created.getTitle()).isEqualTo("Interview preparation");
		assertThat(created.getStatus()).isEqualTo(TaskStatus.PENDING);
		assertThat(created.getCreatedAt()).isNotNull();
		assertThat(created.getUpdatedAt()).isNotNull();

		ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
		verify(taskRepository).save(captor.capture());
		assertThat(captor.getValue().getStatus()).isEqualTo(TaskStatus.PENDING);
	}

	@Test
	void complete_shouldSetStatusToCompleted() {
		Task existing = Task.builder()
				.id(5L)
				.title("Ship feature")
				.status(TaskStatus.IN_PROGRESS)
				.createdAt(LocalDateTime.now().minusDays(2))
				.updatedAt(LocalDateTime.now().minusDays(1))
				.build();

		when(taskRepository.findById(5L)).thenReturn(Optional.of(existing));
		when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

		TaskResponseDto result = taskService.complete(5L);

		assertThat(result.getStatus()).isEqualTo(TaskStatus.COMPLETED);
		assertThat(result.getUpdatedAt()).isNotNull();
	}

	@Test
	void complete_shouldThrowWhenAlreadyCompleted() {
		Task existing = Task.builder()
				.id(9L)
				.title("Done work")
				.status(TaskStatus.COMPLETED)
				.build();

		when(taskRepository.findById(9L)).thenReturn(Optional.of(existing));

		assertThatThrownBy(() -> taskService.complete(9L))
				.isInstanceOf(InvalidTaskException.class)
				.hasMessageContaining("already completed");
	}

	@Test
	void getById_shouldThrowWhenMissing() {
		when(taskRepository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> taskService.getById(99L))
				.isInstanceOf(TaskNotFoundException.class)
				.hasMessageContaining("99");
	}
}
