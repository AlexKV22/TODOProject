package org.todo.todoproject.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.todo.todoproject.dto.response.PageResponse;
import org.todo.todoproject.dto.response.TaskResponse;

@Mapper(componentModel = "spring")
public interface PageMapper {
    @Mapping(target = "content", source = "content")
    @Mapping(target = "totalElements", source = "totalElements")
    @Mapping(target = "totalPages", source = "totalPages")
    @Mapping(target = "size", source = "size")
    @Mapping(target = "number", source = "number")
    PageResponse entityToDto(Page<TaskResponse> page);
}
