package org.todo.todoproject.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.todo.todoproject.util.TaskStatus;

public record TaskChangeStatusRequest(
        @NotNull(message = "Статус задачи не может быть null")
        @Schema(name = "Статус задачи")
        TaskStatus status
) {}
