package org.todo.todoproject.dto.response;

import java.util.List;

public record PageResponse(
        List<TaskResponse> content,
        Long totalElements,
        Integer totalPages,
        Integer size,
        Integer number
) {}
