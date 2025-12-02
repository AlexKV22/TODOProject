package org.todo.todoproject.dto.request;

import jakarta.validation.constraints.NotNull;
import org.todo.todoproject.util.TaskStatus;

public record TaskChangeStatusRequest(
        @NotNull(message = "Статус задачи не может быть null")
        TaskStatus status
) {}
