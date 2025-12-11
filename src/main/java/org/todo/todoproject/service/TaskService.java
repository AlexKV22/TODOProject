package org.todo.todoproject.service;

import org.springframework.data.domain.Page;
import org.todo.todoproject.dto.request.TaskChangeStatusRequest;
import org.todo.todoproject.dto.request.TaskRequest;
import org.todo.todoproject.dto.response.TaskResponse;
import org.todo.todoproject.entity.Task;

import java.util.List;

public interface TaskService {
    TaskResponse createTask(TaskRequest taskRequest);
    TaskResponse updateTask(TaskRequest taskRequest, Long id);
    boolean deleteTask(Long id);
    TaskResponse changeStatus(TaskChangeStatusRequest taskChangeStatusRequest, Long id);
    TaskResponse getTask(Long id);
    Page<TaskResponse> getTasks(Integer page, Integer size);
}