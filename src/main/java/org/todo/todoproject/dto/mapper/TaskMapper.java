package org.todo.todoproject.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.todo.todoproject.dto.request.TaskRequest;
import org.todo.todoproject.dto.response.TaskResponse;
import org.todo.todoproject.entity.Task;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "expireAt", source = "expireAt")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "createAt", source = "createAt")
    TaskResponse entityToDto(Task task);


    @Mapping(target = "title", source = "title")
    @Mapping(target = "expireAt", source = "expireAt")
    Task dtoToEntity(TaskRequest taskRequest);
}
