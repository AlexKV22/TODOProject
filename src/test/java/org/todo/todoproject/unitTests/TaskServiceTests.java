package org.todo.todoproject.unitTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.todo.todoproject.dto.request.TaskChangeStatusRequest;
import org.todo.todoproject.dto.request.TaskRequest;
import org.todo.todoproject.dto.response.TaskResponse;
import org.todo.todoproject.dto.util.TaskServiceDto;
import org.todo.todoproject.entity.Task;
import org.todo.todoproject.exception.NotExistTaskByIDException;
import org.todo.todoproject.exception.NotIDWhenSaveTaskException;
import org.todo.todoproject.repository.TaskRepositoryImpl;
import org.todo.todoproject.service.TaskServiceImpl;
import org.todo.todoproject.util.TaskStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@DisplayName(value = "Тесты сервиса")
class TaskServiceTests {

    @Mock
    private TaskRepositoryImpl taskRepository;
    @Mock
    private TaskServiceDto taskServiceDto;
    @InjectMocks
    private TaskServiceImpl taskServiceImpl;


    @Test
    @DisplayName(value = "Валидные тесты создания задачи")
    void createTaskTest() {
        TaskRequest taskRequest = new TaskRequest("Create task", LocalDate.now());
        Task taskFromRequest = Task.builder().title("Create task").expireAt(LocalDate.now()).build();
        Task taskAfterSave = Task.builder().id(1L).title("Create task").createAt(Instant.now()).expireAt(LocalDate.now()).status(TaskStatus.CREATED).build();
        TaskResponse taskResponse = new TaskResponse(1L, "Create task", Instant.now(), LocalDate.now(), TaskStatus.CREATED);

        Mockito.when(taskServiceDto.dtoToEntity(any(TaskRequest.class))).thenReturn(taskFromRequest);
        Mockito.when(taskRepository.save(taskFromRequest)).thenReturn(taskAfterSave);
        Mockito.when(taskServiceDto.entityToDto(taskAfterSave)).thenReturn(taskResponse);

        TaskResponse task = taskServiceImpl.createTask(taskRequest);
        Assertions.assertEquals(taskResponse.id(), task.id());
        Assertions.assertEquals(taskResponse.title(), task.title());
        Assertions.assertEquals(taskResponse.expireAt(), task.expireAt());
        Assertions.assertEquals(TaskStatus.CREATED, task.status());
        Mockito.verify(taskRepository).save(taskFromRequest);
        Mockito.verify(taskServiceDto).dtoToEntity(any(TaskRequest.class));
        Mockito.verify(taskServiceDto).entityToDto(taskAfterSave);
    }

    @Test
    @DisplayName(value = "Невалидные тесты создания задачи")
    void invalidCreateTaskTest() {
        TaskRequest taskRequest = new TaskRequest("Create task", LocalDate.now());
        Task taskFromRequest = Task.builder().title("Create task").expireAt(LocalDate.now()).build();
        Task taskAfterSave = Task.builder().id(null).title("Create task").createAt(Instant.now()).expireAt(LocalDate.now()).status(TaskStatus.CREATED).build();

        Mockito.when(taskServiceDto.dtoToEntity(any(TaskRequest.class))).thenReturn(taskFromRequest);
        Mockito.when(taskRepository.save(taskFromRequest)).thenReturn(taskAfterSave);

        Assertions.assertThrows(NotIDWhenSaveTaskException.class, () -> taskServiceImpl.createTask(taskRequest));
        Mockito.verify(taskRepository).save(taskFromRequest);
        Mockito.verify(taskServiceDto).dtoToEntity(any(TaskRequest.class));
    }

    @Test
    @DisplayName(value = "Валидные тесты обновления задачи")
    void updateTaskTest() {
        TaskRequest taskRequest = new TaskRequest("Modify task", LocalDate.now());
        Task taskFromRequest = Task.builder().title("Modify task").expireAt(LocalDate.now()).build();
        Task taskUpdated = Task.builder().id(1L).title("Modify task").createAt(Instant.now()).expireAt(LocalDate.now()).status(TaskStatus.UPDATED).build();
        TaskResponse taskResponse = new TaskResponse(1L, "Modify task", Instant.now(), LocalDate.now(), TaskStatus.UPDATED);

        Mockito.when(taskServiceDto.dtoToEntity(any(TaskRequest.class))).thenReturn(taskFromRequest);
        Mockito.when(taskRepository.update(any(Task.class), any(Long.class))).thenReturn(Optional.of(taskUpdated));
        Mockito.when(taskServiceDto.entityToDto(taskUpdated)).thenReturn(taskResponse);

        TaskResponse task = taskServiceImpl.updateTask(taskRequest, 1L);
        Assertions.assertEquals(taskResponse.id(), task.id());
        Assertions.assertEquals(taskResponse.title(), task.title());
        Assertions.assertEquals(taskResponse.expireAt(), task.expireAt());
        Assertions.assertEquals(TaskStatus.UPDATED, task.status());
        Mockito.verify(taskServiceDto).entityToDto(taskUpdated);
        Mockito.verify(taskRepository).update(any(Task.class), any(Long.class));
        Mockito.verify(taskServiceDto).dtoToEntity(any(TaskRequest.class));
    }

    @Test
    @DisplayName(value = "Невалидные тесты обновления задачи")
    void updateTaskWhenNotFoundTaskByIdTest() {
        TaskRequest taskRequest = new TaskRequest("Modify task", LocalDate.now());
        Task taskFromRequest = Task.builder().title("Modify task").expireAt(LocalDate.now()).build();

        Mockito.when(taskServiceDto.dtoToEntity(any(TaskRequest.class))).thenReturn(taskFromRequest);
        Mockito.when(taskRepository.update(any(Task.class), any(Long.class))).thenReturn(Optional.empty());

        Assertions.assertThrows(NotExistTaskByIDException.class, () -> taskServiceImpl.updateTask(taskRequest, 1L));

        Mockito.verify(taskRepository).update(any(Task.class), any(Long.class));
        Mockito.verify(taskServiceDto).dtoToEntity(any(TaskRequest.class));
    }

    @Test
    @DisplayName(value = "Валидные тесты удаления задачи")
    void deleteTaskTest() {
        Mockito.when(taskRepository.delete(any(Long.class))).thenReturn(true);

        boolean result = taskServiceImpl.deleteTask(1L);
        Assertions.assertTrue(result);
        Mockito.verify(taskRepository).delete(any(Long.class));
    }

    @Test
    @DisplayName(value = "Невалидные тесты удаления задачи")
    void deleteTaskWhenNotFoundTaskTest() {
        Mockito.when(taskRepository.delete(any(Long.class))).thenReturn(false);

        Assertions.assertThrows(NotExistTaskByIDException.class, () -> taskServiceImpl.deleteTask(1L));

        Mockito.verify(taskRepository).delete(any(Long.class));
    }

    @Test
    @DisplayName(value = "Невалидные тесты удаления задачи")
    void deleteTaskWithInvalidDatabaseTest() {
        Mockito.when(taskRepository.delete(any(Long.class))).thenThrow(DataAccessResourceFailureException.class);

        Assertions.assertThrows(DataAccessException.class, () -> taskServiceImpl.deleteTask(1L));
        Mockito.verify(taskRepository).delete(any(Long.class));
    }

    @Test
    @DisplayName(value = "Валидные тесты обновления статуса задачи")
    void changeStatusTaskTest() {
        TaskChangeStatusRequest taskChangeStatusRequest = new TaskChangeStatusRequest(TaskStatus.EXPIRED);
        Task taskUpdatedStatus = Task.builder().id(1L).title("Create task").createAt(Instant.now()).expireAt(LocalDate.now()).status(TaskStatus.EXPIRED).build();
        TaskResponse taskResponse = new TaskResponse(1L, "Create task", Instant.now(), LocalDate.now(), TaskStatus.EXPIRED);

        Mockito.when(taskRepository.changeStatus(any(TaskChangeStatusRequest.class), any(Long.class))).thenReturn(Optional.of(taskUpdatedStatus));
        Mockito.when(taskServiceDto.entityToDto(taskUpdatedStatus)).thenReturn(taskResponse);

        TaskResponse task = taskServiceImpl.changeStatus(taskChangeStatusRequest, 1L);
        Assertions.assertEquals(taskResponse.id(), task.id());
        Assertions.assertEquals(taskResponse.title(), task.title());
        Assertions.assertEquals(taskResponse.expireAt(), task.expireAt());
        Assertions.assertEquals(TaskStatus.EXPIRED, task.status());
        Mockito.verify(taskServiceDto).entityToDto(taskUpdatedStatus);
        Mockito.verify(taskRepository).changeStatus(any(TaskChangeStatusRequest.class), any(Long.class));
    }

    @Test
    @DisplayName(value = "Невалидные тесты обновления статуса задачи")
    void changeStatusTaskWhenNotFoundTaskByIdTest() {
        TaskChangeStatusRequest taskChangeStatusRequest = new TaskChangeStatusRequest(TaskStatus.EXPIRED);

        Mockito.when(taskRepository.changeStatus(any(TaskChangeStatusRequest.class), any(Long.class))).thenReturn(Optional.empty());

        Assertions.assertThrows(NotExistTaskByIDException.class, () -> taskServiceImpl.changeStatus(taskChangeStatusRequest,1L));

        Mockito.verify(taskRepository).changeStatus(any(TaskChangeStatusRequest.class), any(Long.class));
    }

    @Test
    @DisplayName(value = "Невалидные тесты обновления статуса задачи")
    void changeStatusTaskWithInvalidDatabaseTest() {
        TaskChangeStatusRequest taskChangeStatusRequest = new TaskChangeStatusRequest(TaskStatus.EXPIRED);
        Mockito.when(taskRepository.changeStatus(any(TaskChangeStatusRequest.class), any(Long.class))).thenThrow(DataAccessResourceFailureException.class);

        Assertions.assertThrows(DataAccessException.class, () -> taskServiceImpl.changeStatus(taskChangeStatusRequest,1L));
        Mockito.verify(taskRepository).changeStatus(any(TaskChangeStatusRequest.class), any(Long.class));
    }

    @Test
    @DisplayName(value = "Валидные тесты получения задачи пой айди")
    void getTaskByIdTest() {
        Task taskFromDatabase = Task.builder().id(1L).title("Create task").createAt(Instant.now()).expireAt(LocalDate.now()).status(TaskStatus.CREATED).build();
        TaskResponse taskResponse = new TaskResponse(1L, "Create task", Instant.now(), LocalDate.now(), TaskStatus.EXPIRED);

        Mockito.when(taskRepository.findById(any(Long.class))).thenReturn(Optional.of(taskFromDatabase));
        Mockito.when(taskServiceDto.entityToDto(taskFromDatabase)).thenReturn(taskResponse);

        TaskResponse task = taskServiceImpl.getTask(1L);
        Assertions.assertEquals(taskResponse.id(), task.id());
        Assertions.assertEquals(taskResponse.title(), task.title());
        Assertions.assertEquals(taskResponse.expireAt(), task.expireAt());
        Assertions.assertEquals(taskResponse.status(), task.status());
        Mockito.verify(taskServiceDto).entityToDto(taskFromDatabase);
        Mockito.verify(taskRepository).findById(any(Long.class));
    }

    @Test
    @DisplayName(value = "Невалидные тесты получения задачи пой айди")
    void getTaskByIdWhenNotFoundTaskTest() {
        Mockito.when(taskRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        Assertions.assertThrows(NotExistTaskByIDException.class, () -> taskServiceImpl.getTask(1L));
        Mockito.verify(taskRepository).findById(any(Long.class));
    }

    @Test
    @DisplayName(value = "Невалидные тесты получения задачи пой айди")
    void getTaskByIdWithInvalidDatabaseTest() {
        Mockito.when(taskRepository.findById(any(Long.class))).thenThrow(DataAccessResourceFailureException.class);

        Assertions.assertThrows(DataAccessException.class, () -> taskServiceImpl.getTask(1L));
        Mockito.verify(taskRepository).findById(any(Long.class));
    }

    @Test
    @DisplayName(value = "Валидные тесты получения всех задач")
    void getAllTasksTest() {
        Task taskOne = Task.builder().id(1L).title("Create task").createAt(Instant.now()).expireAt(LocalDate.now()).status(TaskStatus.CREATED).build();
        Task taskTwo = Task.builder().id(2L).title("Enouth task").createAt(Instant.now()).expireAt(LocalDate.now()).status(TaskStatus.CREATED).build();
        List<Task> listTasks = List.of(taskOne, taskTwo);

        Page<Task> tasks = new PageImpl<>(listTasks);
        Pageable pageable = PageRequest.of(0, 10);

        TaskResponse taskResponseOne = new TaskResponse(1L, "Create task", Instant.now(), LocalDate.now(), TaskStatus.CREATED);
        TaskResponse taskResponseTwo = new TaskResponse(2L, "Enouth task", Instant.now(), LocalDate.now(), TaskStatus.CREATED);

        Mockito.when(taskRepository.findAll(any(Pageable.class))).thenReturn(tasks);
        Mockito.when(taskServiceDto.entityToDto(taskOne)).thenReturn(taskResponseOne);
        Mockito.when(taskServiceDto.entityToDto(taskTwo)).thenReturn(taskResponseTwo);

        Page<TaskResponse> result = taskServiceImpl.getTasks(0, 10);
        Assertions.assertEquals(2, result.getTotalElements());
        Assertions.assertEquals(taskResponseOne.id(), result.getContent().get(0).id());
        Assertions.assertEquals(taskResponseTwo.id(), result.getContent().get(1).id());
        Mockito.verify(taskRepository).findAll(any(Pageable.class));
        Mockito.verify(taskServiceDto).entityToDto(taskOne);
        Mockito.verify(taskServiceDto).entityToDto(taskTwo);
    }

    @Test
    @DisplayName(value = "Валидные тесты получения всех задач")
    void getEmptyTasksTest() {
        List<Task> listTasks = Collections.emptyList();
        Page<Task> tasks = new PageImpl<>(listTasks);

        Mockito.when(taskRepository.findAll(any(Pageable.class))).thenReturn(tasks);

        Page<TaskResponse> result = taskServiceImpl.getTasks(0, 10);
        Assertions.assertEquals(0, result.getTotalElements());
        Mockito.verify(taskRepository).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName(value = "Невалидные тесты получения всех задач")
    void getAllTasksWithInvalidDatabaseTestTest() {
        Mockito.when(taskRepository.findAll(any(Pageable.class))).thenThrow(DataAccessResourceFailureException.class);
        Assertions.assertThrows(DataAccessException.class, () -> taskServiceImpl.getTasks(0,10));
        Mockito.verify(taskRepository).findAll(any(Pageable.class));
    }
}
