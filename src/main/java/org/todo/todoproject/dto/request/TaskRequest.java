package org.todo.todoproject.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TaskRequest(
        @NotNull(message = "Описание задачи не может быть null")
        @NotBlank(message = "Описание задачи не может быть пустым")
        @Size(max = 200)
        @Schema(name = "Описание задачи")
        String title,

        @FutureOrPresent(message = "Дата дедлайна может быть либо текущей датой, либо будущей датой")
        @Schema(name = "Дата дедлайна по задаче")
        LocalDate expireAt
) {}
