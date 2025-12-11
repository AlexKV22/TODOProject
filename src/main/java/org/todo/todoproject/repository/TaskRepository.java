package org.todo.todoproject.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.todo.todoproject.dto.request.TaskChangeStatusRequest;
import org.todo.todoproject.entity.Task;
import org.todo.todoproject.util.TaskStatus;

import java.util.List;
import java.util.Optional;


public interface TaskRepository {
    Task save(Task task);
    Page<Task> findAll(Pageable pageable);
    Optional<Task> findById(Long id);
    boolean delete(Long id);
    Optional<Task> update(Task task, Long id);
    Optional<Task> changeStatus(TaskChangeStatusRequest taskChangeStatusRequest, Long id);
}
