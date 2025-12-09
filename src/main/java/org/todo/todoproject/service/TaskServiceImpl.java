package org.todo.todoproject.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.todo.todoproject.dto.request.TaskChangeStatusRequest;
import org.todo.todoproject.dto.request.TaskRequest;
import org.todo.todoproject.dto.response.TaskResponse;
import org.todo.todoproject.dto.util.TaskServiceDto;
import org.todo.todoproject.entity.Task;
import org.todo.todoproject.exception.NotIDWhenSaveTaskException;
import org.todo.todoproject.repository.TaskRepository;
import org.todo.todoproject.util.TaskStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final TaskServiceDto taskServiceDto;
    private final TaskRepository taskRepository;

    @Autowired
    public TaskServiceImpl(TaskServiceDto taskServiceDto, TaskRepository taskRepository) {
        this.taskServiceDto = taskServiceDto;
        this.taskRepository = taskRepository;
    }

    @Override
    @Transactional
    public TaskResponse createTask(TaskRequest taskRequest) {
        Task task = taskServiceDto.dtoToEntity(taskRequest);
        task.setStatus(TaskStatus.CREATED);
        task.setCreateAt(Instant.now());
        task = taskRepository.save(task);
        if (task.getId() != null) {
            createLog(TaskStatus.CREATED, task.getId());
            return taskServiceDto.entityToDto(task);
        } else {
            throw new NotIDWhenSaveTaskException();
        }
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "task", key = "#id"),
            @CacheEvict(value = "allTasks", allEntries = true)
    })
    public TaskResponse updateTask(TaskRequest taskRequest, Long id) {
        Task task = taskServiceDto.dtoToEntity(taskRequest);
        task.setStatus(TaskStatus.UPDATED);
        Optional<Task> updateTask = taskRepository.update(task, id);
        if (updateTask.isPresent()) {
            Task result = updateTask.get();
            createLog(TaskStatus.UPDATED, result.getId());
            return taskServiceDto.entityToDto(result);
        }
        return null;
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "task", key = "#id"),
            @CacheEvict(value = "allTasks", allEntries = true)
    })
    public boolean deleteTask(Long id) {
        boolean delete = taskRepository.delete(id);
        if (delete) {
            createLog(TaskStatus.DELETED, id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "task", key = "#id"),
            @CacheEvict(value = "allTasks", allEntries = true)
    })
    public TaskResponse changeStatus(TaskChangeStatusRequest taskChangeStatusRequest, Long id) {
        Optional<Task> task = taskRepository.changeStatus(taskChangeStatusRequest, id);
        if (task.isPresent()) {
            Task result = task.get();
            createLog(TaskStatus.UPDATED, result.getId());
            return taskServiceDto.entityToDto(result);
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "task", key = "#id")
    public TaskResponse getTask(Long id) {
        return findTaskById(id).map(taskServiceDto::entityToDto).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "allTasks")
    public List<TaskResponse> getTasks(int limit, int offset) {
        return taskRepository.findAll(limit, offset).stream().map(taskServiceDto::entityToDto).toList();
    }

    private Optional<Task> findTaskById(Long id) {
        log.info("Проверка наличия задачи в базе данных с id: {}", id );
        Optional<Task> taskById = taskRepository.findById(id);
        if (taskById.isEmpty()) {
            log.info("Отсутствие в базе задачи с id: {}", id );
        }
        return taskById;
    }

    private void createLog(TaskStatus status, Long id) {
        log.info("Action: {}, id: {}", status, id);
    }
}
