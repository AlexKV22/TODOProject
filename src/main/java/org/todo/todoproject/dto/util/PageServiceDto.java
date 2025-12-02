package org.todo.todoproject.dto.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.todo.todoproject.dto.mapper.PageMapper;
import org.todo.todoproject.dto.response.PageResponse;
import org.todo.todoproject.dto.response.TaskResponse;

@Component
public class PageServiceDto {
    private final PageMapper pageMapper;

    @Autowired
    public PageServiceDto(PageMapper pageMapper) {
        this.pageMapper = pageMapper;
    }

    public PageResponse entityToDto(Page<TaskResponse> page) {
        return pageMapper.entityToDto(page);
    }
}
