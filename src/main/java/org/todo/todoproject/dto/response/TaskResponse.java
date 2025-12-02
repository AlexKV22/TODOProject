package org.todo.todoproject.dto.response;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.todo.todoproject.util.TaskStatus;

import java.time.LocalDate;

public record TaskResponse(
        @NotNull(message = "ID не может быть null")
        @Positive(message = "ID должен быть положительным числом")
        Long id,

        @NotNull(message = "Описание задачи не может быть null")
        @NotBlank(message = "Описание задачи не может быть пустым")
        @Size(max = 200)
        String title,

        @FutureOrPresent(message = "Дата дедлайна может быть либо текущей датой, либо будущей датой")
        LocalDate expireAt,

        @NotNull(message = "Статус задачи не может быть null")
        TaskStatus status
) {}
