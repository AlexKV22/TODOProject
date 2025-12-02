package org.todo.todoproject.unitTests;

import org.junit.jupiter.api.Assertions;
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
import org.todo.todoproject.dto.response.PageResponse;
import org.todo.todoproject.dto.response.TaskResponse;
import org.todo.todoproject.dto.util.PageServiceDto;
import org.todo.todoproject.dto.util.TaskServiceDto;
import org.todo.todoproject.entity.Task;
import org.todo.todoproject.repository.TaskRepository;
import org.todo.todoproject.service.TaskServiceImpl;
import org.todo.todoproject.util.TaskStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class TaskServiceTests {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private TaskServiceDto taskServiceDto;
    @Mock
    private PageServiceDto pageServiceDto;
    @InjectMocks
    private TaskServiceImpl taskServiceImpl;


    @Test
    void createTaskTest() {
        TaskRequest taskRequest = new TaskRequest("Create task", LocalDate.now());
        Task taskFromRequest = Task.builder().title("Create task").expireAt(LocalDate.now()).build();
        Task taskAfterSave = Task.builder().id(1L).title("Create task").createdAt(Instant.now()).expireAt(LocalDate.now()).status(TaskStatus.CREATED).build();
        TaskResponse taskResponse = new TaskResponse(1L, "Create task", LocalDate.now(), TaskStatus.CREATED);

        Mockito.when(taskServiceDto.dtoToEntity(Mockito.any(TaskRequest.class))).thenReturn(taskFromRequest);
        Mockito.when(taskRepository.save(taskFromRequest)).thenReturn(taskAfterSave);
        Mockito.when(taskServiceDto.entityToDto(taskAfterSave)).thenReturn(taskResponse);

        TaskResponse task = taskServiceImpl.createTask(taskRequest);
        Assertions.assertEquals(taskResponse.id(), task.id());
        Assertions.assertEquals(taskResponse.title(), task.title());
        Assertions.assertEquals(taskResponse.expireAt(), task.expireAt());
        Mockito.verify(taskRepository).save(taskFromRequest);
        Mockito.verify(taskServiceDto).dtoToEntity(Mockito.any(TaskRequest.class));
        Mockito.verify(taskServiceDto).entityToDto(taskAfterSave);
    }

    @Test
    void createTaskWithInvalidDatabaseTest() {
        TaskRequest taskRequest = new TaskRequest("Create task", LocalDate.now());
        Task taskFromRequest = Task.builder().title("Create task").expireAt(LocalDate.now()).build();

        Mockito.when(taskServiceDto.dtoToEntity(Mockito.any(TaskRequest.class))).thenReturn(taskFromRequest);
        Mockito.when(taskRepository.save(taskFromRequest)).thenThrow(DataAccessResourceFailureException.class);

        Assertions.assertThrows(DataAccessException.class, () -> taskServiceImpl.createTask(taskRequest));
        Mockito.verify(taskRepository).save(taskFromRequest);
        Mockito.verify(taskServiceDto).dtoToEntity(Mockito.any(TaskRequest.class));
    }

    @Test
    void updateTaskTest() {
        TaskRequest taskRequest = new TaskRequest("Modify task", LocalDate.now());
        Task taskFromDatabase = Task.builder().id(1L).title("Create task").createdAt(Instant.now()).expireAt(LocalDate.now()).status(TaskStatus.CREATED).build();
        Task taskUpdated = Task.builder().id(1L).title("Modify task").createdAt(Instant.now()).expireAt(LocalDate.now()).status(TaskStatus.UPDATED).build();
        TaskResponse taskResponse = new TaskResponse(1L, "Modify task", LocalDate.now(), TaskStatus.UPDATED);

        Mockito.when(taskRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.of(taskFromDatabase));
        Mockito.when(taskRepository.save(Mockito.any(Task.class))).thenReturn(taskUpdated);
        Mockito.when(taskServiceDto.entityToDto(taskUpdated)).thenReturn(taskResponse);

        TaskResponse task = taskServiceImpl.updateTask(taskRequest, 1L);
        Assertions.assertEquals(taskResponse.id(), task.id());
        Assertions.assertEquals(taskResponse.title(), task.title());
        Assertions.assertEquals(taskResponse.expireAt(), task.expireAt());
        Mockito.verify(taskRepository).findById(Mockito.any(Long.class));
        Mockito.verify(taskRepository).save(Mockito.any(Task.class));
        Mockito.verify(taskServiceDto).entityToDto(taskUpdated);
    }

    @Test
    void updateTaskWithInvalidDatabaseTest() {
        TaskRequest taskRequest = new TaskRequest("Modify task", LocalDate.now());

        Mockito.when(taskRepository.findById(Mockito.any(Long.class))).thenThrow(DataAccessResourceFailureException.class);

        Assertions.assertThrows(DataAccessException.class, () -> taskServiceImpl.updateTask(taskRequest, 1L));
        Mockito.verify(taskRepository).findById(Mockito.any(Long.class));
    }

    @Test
    void updateTaskWhenNotFoundTaskByIdTest() {
        TaskRequest taskRequest = new TaskRequest("Modify task", LocalDate.now());

        Mockito.when(taskRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.empty());

        TaskResponse task = taskServiceImpl.updateTask(taskRequest, 1L);
        Assertions.assertNull(task);
        Mockito.verify(taskRepository).findById(Mockito.any(Long.class));
    }

    @Test
    void deleteTaskTest() {
        Task taskFromDatabase = Task.builder().id(1L).title("Create task").createdAt(Instant.now()).expireAt(LocalDate.now()).status(TaskStatus.CREATED).build();

        Mockito.when(taskRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.of(taskFromDatabase));
        Mockito.doNothing().when(taskRepository).delete(taskFromDatabase);

        boolean result = taskServiceImpl.deleteTask(1L);
        Assertions.assertTrue(result);
        Mockito.verify(taskRepository).findById(Mockito.any(Long.class));
        Mockito.verify(taskRepository).delete(taskFromDatabase);
    }

    @Test
    void deleteTaskWhenNotFoundTaskByIdTest() {
        Mockito.when(taskRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.empty());

        boolean result = taskServiceImpl.deleteTask(1L);
        Assertions.assertFalse(result);
        Mockito.verify(taskRepository).findById(Mockito.any(Long.class));
    }

    @Test
    void deleteTaskWithInvalidDatabaseTest() {
        Mockito.when(taskRepository.findById(Mockito.any(Long.class))).thenThrow(DataAccessResourceFailureException.class);

        Assertions.assertThrows(DataAccessException.class, () -> taskServiceImpl.deleteTask(1L));
        Mockito.verify(taskRepository).findById(Mockito.any(Long.class));
    }

    @Test
    void changeStatusTaskTest() {
        TaskChangeStatusRequest taskChangeStatusRequest = new TaskChangeStatusRequest(TaskStatus.EXPIRED);
        Task taskFromDatabase = Task.builder().id(1L).title("Create task").createdAt(Instant.now()).expireAt(LocalDate.now()).status(TaskStatus.CREATED).build();
        Task taskUpdatedStatus = Task.builder().id(1L).title("Create task").createdAt(Instant.now()).expireAt(LocalDate.now()).status(TaskStatus.EXPIRED).build();
        TaskResponse taskResponse = new TaskResponse(1L, "Create task", LocalDate.now(), TaskStatus.EXPIRED);

        Mockito.when(taskRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.of(taskFromDatabase));
        Mockito.when(taskRepository.save(Mockito.any(Task.class))).thenReturn(taskUpdatedStatus);
        Mockito.when(taskServiceDto.entityToDto(taskUpdatedStatus)).thenReturn(taskResponse);

        TaskResponse task = taskServiceImpl.changeStatus(taskChangeStatusRequest, 1L);
        Assertions.assertEquals(taskResponse.id(), task.id());
        Assertions.assertEquals(taskResponse.title(), task.title());
        Assertions.assertEquals(taskResponse.expireAt(), task.expireAt());
        Assertions.assertEquals(TaskStatus.EXPIRED, task.status());
        Mockito.verify(taskRepository).save(Mockito.any(Task.class));
        Mockito.verify(taskServiceDto).entityToDto(taskUpdatedStatus);
        Mockito.verify(taskRepository).findById(Mockito.any(Long.class));
    }

    @Test
    void changeStatusTaskWhenNotFoundTaskByIdTest() {
        TaskChangeStatusRequest taskChangeStatusRequest = new TaskChangeStatusRequest(TaskStatus.EXPIRED);

        Mockito.when(taskRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.empty());

        TaskResponse taskResponse = taskServiceImpl.changeStatus(taskChangeStatusRequest, 1L);
        Assertions.assertNull(taskResponse);
        Mockito.verify(taskRepository).findById(Mockito.any(Long.class));
    }

    @Test
    void changeStatusTaskWithInvalidDatabaseTest() {
        TaskChangeStatusRequest taskChangeStatusRequest = new TaskChangeStatusRequest(TaskStatus.EXPIRED);
        Mockito.when(taskRepository.findById(Mockito.any(Long.class))).thenThrow(DataAccessResourceFailureException.class);

        Assertions.assertThrows(DataAccessException.class, () -> taskServiceImpl.changeStatus(taskChangeStatusRequest,1L));
        Mockito.verify(taskRepository).findById(Mockito.any(Long.class));
    }

    @Test
    void getTaskByIdTest() {
        Task taskFromDatabase = Task.builder().id(1L).title("Create task").createdAt(Instant.now()).expireAt(LocalDate.now()).status(TaskStatus.CREATED).build();
        TaskResponse taskResponse = new TaskResponse(1L, "Create task", LocalDate.now(), TaskStatus.EXPIRED);

        Mockito.when(taskRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.of(taskFromDatabase));
        Mockito.when(taskServiceDto.entityToDto(taskFromDatabase)).thenReturn(taskResponse);

        TaskResponse task = taskServiceImpl.getTask(1L);
        Assertions.assertEquals(taskResponse.id(), task.id());
        Assertions.assertEquals(taskResponse.title(), task.title());
        Assertions.assertEquals(taskResponse.expireAt(), task.expireAt());
        Assertions.assertEquals(taskResponse.status(), task.status());
        Mockito.verify(taskServiceDto).entityToDto(taskFromDatabase);
        Mockito.verify(taskRepository).findById(Mockito.any(Long.class));
    }

    @Test
    void getTaskByIdWhenNotFoundTaskByIdTest() {
        Mockito.when(taskRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.empty());

        TaskResponse task = taskServiceImpl.getTask(1L);
        Assertions.assertNull(task);
        Mockito.verify(taskRepository).findById(Mockito.any(Long.class));
    }

    @Test
    void getTaskByIdWithInvalidDatabaseTest() {
        Mockito.when(taskRepository.findById(Mockito.any(Long.class))).thenThrow(DataAccessResourceFailureException.class);

        Assertions.assertThrows(DataAccessException.class, () -> taskServiceImpl.getTask(1L));
        Mockito.verify(taskRepository).findById(Mockito.any(Long.class));
    }

    @Test
    void getAllTasksTest() {
        Task taskOne = Task.builder().id(1L).title("Create task").createdAt(Instant.now()).expireAt(LocalDate.now()).status(TaskStatus.CREATED).build();
        Task taskTwo = Task.builder().id(2L).title("Enouth task").createdAt(Instant.now()).expireAt(LocalDate.now()).status(TaskStatus.CREATED).build();
        List<Task> listTasks = List.of(taskOne, taskTwo);
        Page<Task> tasks = new PageImpl<>(listTasks, PageRequest.of(0, 10),
                2);

        TaskResponse taskResponseOne = new TaskResponse(1L, "Create task", LocalDate.now(), TaskStatus.CREATED);
        TaskResponse taskResponseTwo = new TaskResponse(2L, "Enouth task", LocalDate.now(), TaskStatus.CREATED);
        List<TaskResponse> listTaskResponses = List.of(taskResponseOne, taskResponseTwo);

        PageResponse pageResponse = new PageResponse(listTaskResponses, 2L, 1, 1, 0);

        Mockito.when(taskRepository.findAll(Mockito.any(Pageable.class))).thenReturn(tasks);
        Mockito.when(taskServiceDto.entityToDto(taskOne)).thenReturn(taskResponseOne);
        Mockito.when(taskServiceDto.entityToDto(taskTwo)).thenReturn(taskResponseTwo);
        Mockito.when(pageServiceDto.entityToDto(Mockito.any(Page.class))).thenReturn(pageResponse);

        PageResponse result = taskServiceImpl.getTasks(0, 10);
        Assertions.assertEquals(pageResponse.totalElements(), result.totalElements());
        Assertions.assertEquals(pageResponse.totalPages(), result.totalPages());
        Assertions.assertEquals(pageResponse.number(), result.number());
        Mockito.verify(taskRepository).findAll(Mockito.any(Pageable.class));
        Mockito.verify(taskServiceDto).entityToDto(taskOne);
        Mockito.verify(taskServiceDto).entityToDto(taskTwo);
        Mockito.verify(pageServiceDto).entityToDto(Mockito.any(Page.class));
    }

    @Test
    void getEmptyTasksTest() {
        Page<Task> tasks = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10),
                0);
        List<TaskResponse> listTaskResponses = Collections.emptyList();

        PageResponse pageResponse = new PageResponse(listTaskResponses, 0L, 0, 10, 0);

        Mockito.when(taskRepository.findAll(Mockito.any(Pageable.class))).thenReturn(tasks);
        Mockito.when(pageServiceDto.entityToDto(Mockito.any(Page.class))).thenReturn(pageResponse);

        PageResponse result = taskServiceImpl.getTasks(0, 10);
        Assertions.assertEquals(0, result.totalElements());
        Assertions.assertEquals(0, result.totalPages());
        Assertions.assertEquals(0, result.number());
        Mockito.verify(taskRepository).findAll(Mockito.any(Pageable.class));
        Mockito.verify(pageServiceDto).entityToDto(Mockito.any(Page.class));

    }

    @Test
    void getAllTasksWithInvalidDatabaseTestTest() {
        Mockito.when(taskRepository.findAll(Mockito.any(Pageable.class))).thenThrow(DataAccessResourceFailureException.class);
        Assertions.assertThrows(DataAccessException.class, () -> taskServiceImpl.getTasks(0,10));
        Mockito.verify(taskRepository).findAll(Mockito.any(Pageable.class));
    }
}
