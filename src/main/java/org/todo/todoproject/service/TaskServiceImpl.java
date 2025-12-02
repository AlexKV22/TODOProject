package org.todo.todoproject.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.todo.todoproject.dto.request.TaskChangeStatusRequest;
import org.todo.todoproject.dto.request.TaskRequest;
import org.todo.todoproject.dto.response.PageResponse;
import org.todo.todoproject.dto.response.TaskResponse;
import org.todo.todoproject.dto.util.PageServiceDto;
import org.todo.todoproject.dto.util.TaskServiceDto;
import org.todo.todoproject.entity.Task;
import org.todo.todoproject.repository.TaskRepository;
import org.todo.todoproject.util.TaskStatus;

import java.util.Optional;


@Service
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final TaskServiceDto taskServiceDto;
    private final PageServiceDto pageServiceDto;
    private final TaskRepository taskRepository;

    @Autowired
    public TaskServiceImpl(TaskServiceDto taskServiceDto, TaskRepository taskRepository, PageServiceDto pageServiceDto) {
        this.taskServiceDto = taskServiceDto;
        this.taskRepository = taskRepository;
        this.pageServiceDto = pageServiceDto;
    }

    @Override
    @Transactional
    public TaskResponse createTask(TaskRequest taskRequest) {
        Task task = taskServiceDto.dtoToEntity(taskRequest);
        task.setStatus(TaskStatus.CREATED);
        task = taskRepository.save(task);
        createLog(TaskStatus.CREATED, task.getId(), task.getTitle());
        return taskServiceDto.entityToDto(task);
    }

    @Override
    @Transactional
    public TaskResponse updateTask(TaskRequest taskRequest, Long id) {
        Optional<Task> taskById = findTaskById(id);
        if (taskById.isPresent()) {
            Task task = taskById.get();
            task.setTitle(taskRequest.title());
            task.setExpireAt(taskRequest.expireAt());
            task.setStatus(TaskStatus.UPDATED);
            task = taskRepository.save(task);
            createLog(TaskStatus.UPDATED, task.getId(), task.getTitle());
            return taskServiceDto.entityToDto(task);
        }
        return null;
    }

    @Override
    @Transactional
    public boolean deleteTask(Long id) {
        Optional<Task> taskById = findTaskById(id);
        if (taskById.isPresent()) {
            Task task = taskById.get();
            taskRepository.delete(task);
            createLog(TaskStatus.DELETED, task.getId(), task.getTitle());
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public TaskResponse changeStatus(TaskChangeStatusRequest taskChangeStatusRequest, Long id) {
        Optional<Task> taskById = findTaskById(id);
        if (taskById.isPresent()) {
            Task task = taskById.get();
            task.setStatus(taskChangeStatusRequest.status());
            task = taskRepository.save(task);
            createLog(TaskStatus.UPDATED, task.getId(), task.getTitle());
            return taskServiceDto.entityToDto(task);
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTask(Long id) {
        return findTaskById(id).map(taskServiceDto::entityToDto).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse getTasks(int page, int size) {
        Page<TaskResponse> map = taskRepository.findAll(PageRequest.of(page, size)).map(task -> taskServiceDto.entityToDto(task));
        return pageServiceDto.entityToDto(map);
    }

    private Optional<Task> findTaskById(Long id) {
        log.info("Проверка наличия задачи в базе данных с id: {}", id );
        Optional<Task> taskById = taskRepository.findById(id);
        if (taskById.isEmpty()) {
            log.info("Отсутствие в базе задачи с id: {}", id );
        }
        return taskById;
    }

    private void createLog(TaskStatus status, Long id, String title) {
        log.info("Action: {}, id: {}, title: {}", status, id, title);
    }
}
