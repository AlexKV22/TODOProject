package org.todo.todoproject.service;

import org.todo.todoproject.dto.request.TaskChangeStatusRequest;
import org.todo.todoproject.dto.request.TaskRequest;
import org.todo.todoproject.dto.response.TaskResponse;

import java.util.List;

public interface TaskService {
    TaskResponse createTask(TaskRequest taskRequest);
    TaskResponse updateTask(TaskRequest taskRequest, Long id);
    boolean deleteTask(Long id);
    TaskResponse changeStatus(TaskChangeStatusRequest taskChangeStatusRequest, Long id);
    TaskResponse getTask(Long id);
    List<TaskResponse> getTasks(int page, int size);
}