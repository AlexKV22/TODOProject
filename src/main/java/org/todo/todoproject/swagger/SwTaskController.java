package org.todo.todoproject.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.todo.todoproject.dto.request.TaskChangeStatusRequest;
import org.todo.todoproject.dto.request.TaskRequest;
import org.todo.todoproject.dto.response.TaskResponse;

import java.util.List;

@Tag(name = "Контроллер задач", description = "Контроллер для управления запросами для сущности Task")
public interface SwTaskController {

    @Operation(
            summary = "Получение задач",

            description = "Получение всего списка задач",
            responses = {
                    @ApiResponse(
                            description = "Задачи получены",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                            [
                                                                 {
                                                                     "id": 2,
                                                                     "title": "56",
                                                                     "createAt": "2025-12-07T14:14:45.622332Z",
                                                                     "expireAt": "2025-12-07",
                                                                     "status": "CREATED"
                                                                 },
                                                                 {
                                                                     "id": 1,
                                                                     "title": "56",
                                                                     "createAt": "2025-12-07T10:06:26.086596Z",
                                                                     "expireAt": null,
                                                                     "status": "UPDATED"
                                                                 }
                                                             ]
                                                            """
                                            )
                                    }

                            )),
                    @ApiResponse(
                            description = "Список задач пуст",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                            []
                                                            """
                                            )
                                    }

                            )),

            }
    )
    Page<TaskResponse> getAllTasks(@RequestParam(defaultValue = "0") @PositiveOrZero Integer page,
                                   @RequestParam(defaultValue = "10") @Positive Integer size);


    @Operation(
            summary = "Получение одной задачи",

            description = "Получение одной задачи по айди",
            responses = {
                    @ApiResponse(
                            description = "Задача получена",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                            {
                                                                "id": 1,
                                                                "title": "Find a book",
                                                                "createdAt": "2023-10-31T18:09:02.598535700Z",
                                                                "expireAt": "2023-10-31",
                                                                "status": "CREATED"
                                                            }
                                                            """
                                            )
                                    }

                            )),
                    @ApiResponse(
                            description = "Задача не найдена",
                            responseCode = "404"
                            )
            }
    )
    TaskResponse getTaskById(@PathVariable @Positive Long id);


    @Operation(
            summary = "Создание одной задачи",

            description = "Создание задачи",
            responses = {
                    @ApiResponse(
                            description = "Задача создана",
                            responseCode = "201",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                            {
                                                                "id": 1,
                                                                "title": "Find a book",
                                                                "createdAt": "2023-10-31T18:09:02.598535700Z",
                                                                "expireAt": "2023-10-31",
                                                                "status": "CREATED"
                                                            }
                                                            """
                                            )
                                    }

                            )),
            }
    )
    TaskResponse createTask(@RequestBody @Valid TaskRequest taskRequest);

    @Operation(
            summary = "Обновление задачи",

            description = "Обновление задачи по айди",
            responses = {
                    @ApiResponse(
                            description = "Задача обновлена",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                            {
                                                                "id": 1,
                                                                "title": "Find a book",
                                                                "createdAt": "2023-10-31T18:09:02.598535700Z",
                                                                "expireAt": "2023-10-31",
                                                                "status": "UPDATED"
                                                            }
                                                            """
                                            )
                                    }

                            )),
                    @ApiResponse(
                            description = "Задача не найдена",
                            responseCode = "404"
                            )
            }
    )
    TaskResponse updateTask(@RequestBody @Valid TaskRequest taskRequest, @PathVariable @Positive Long id);


    @Operation(
            summary = "Удаление задачи",

            description = "Удаление задачи по айди",
            responses = {
                    @ApiResponse(
                            description = "Задача удалена",
                            responseCode = "204"
                            ),
                    @ApiResponse(
                            description = "Задачи не найдена для удаления",
                            responseCode = "404"
                            )
            }
    )
    void deleteTask(@PathVariable @Positive Long id);


    @Operation(
            summary = "Обновление статуса задачи",

            description = "Обновление статуса задачи по айди",
            responses = {
                    @ApiResponse(
                            description = "Статус обновлен",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                            {
                                                                "id": 1,
                                                                "title": "Find a book",
                                                                "createdAt": "2023-10-31T18:09:02.598535700Z",
                                                                "expireAt": "2023-10-31",
                                                                "status": "EXPIRED"
                                                            }
                                                            """
                                            )
                                    }

                            )),
                    @ApiResponse(
                            description = "Задача не найдена",
                            responseCode = "404"
                    )
            }
    )
    TaskResponse changeStatusTask(@RequestBody @Valid TaskChangeStatusRequest taskChangeStatusRequest, @PathVariable @Positive Long id);
}
