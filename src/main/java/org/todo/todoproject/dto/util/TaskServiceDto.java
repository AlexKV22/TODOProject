package org.todo.todoproject.dto.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.todo.todoproject.dto.mapper.TaskMapper;
import org.todo.todoproject.dto.request.TaskRequest;
import org.todo.todoproject.dto.response.TaskResponse;
import org.todo.todoproject.entity.Task;

@Component
public class TaskServiceDto {
    private final TaskMapper taskMapper;

    @Autowired
    public TaskServiceDto(TaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }

    public Task dtoToEntity(TaskRequest taskRequest) {
        return taskMapper.dtoToEntity(taskRequest);
    }

    public TaskResponse entityToDto(Task task) {
        return taskMapper.entityToDto(task);
    }
}
