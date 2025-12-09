package org.todo.todoproject.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.todo.todoproject.util.TaskStatus;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

public record TaskResponse(
        @NotNull(message = "ID не может быть null")
        @Positive(message = "ID должен быть положительным числом")
        @Schema(name = "Айди задачи")
        Long id,

        @NotNull(message = "Описание задачи не может быть null")
        @NotBlank(message = "Описание задачи не может быть пустым")
        @Size(max = 200)
        @Schema(name = "Описание задачи")
        String title,

        @PastOrPresent(message = "Дата создания может быть либо текущей датой, либо прошедшей датой")
        @Schema(name = "Дата создания задачи")
        Instant createAt,

        @FutureOrPresent(message = "Дата дедлайна может быть либо текущей датой, либо будущей датой")
        @Schema(name = "Дата дедлайна задачи")
        LocalDate expireAt,

        @NotNull(message = "Статус задачи не может быть null")
        @Schema(name = "Статус задачи")
        TaskStatus status
) implements Serializable {}
