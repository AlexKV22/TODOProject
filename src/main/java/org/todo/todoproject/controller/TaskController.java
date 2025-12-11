package org.todo.todoproject.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.todo.todoproject.dto.request.TaskChangeStatusRequest;
import org.todo.todoproject.dto.request.TaskRequest;
import org.todo.todoproject.dto.response.TaskResponse;
import org.todo.todoproject.service.TaskService;
import org.todo.todoproject.swagger.SwTaskController;
import org.todo.todoproject.util.TaskStatus;


@RestController
@RequestMapping("/")
@Validated
public class TaskController implements SwTaskController {

    private final TaskService taskService;
    private final Counter taskCounter;

    @Autowired
    public TaskController(TaskService taskService, MeterRegistry registry) {
        this.taskService = taskService;
        this.taskCounter = Counter.builder("tasks.finish")
                .description("Какое количество задач завершено")
                .tag("status", "completed")
                .register(registry);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<TaskResponse> getAllTasks(@RequestParam(defaultValue = "0") @PositiveOrZero Integer page,
                                                          @RequestParam(defaultValue = "10") @Positive Integer size) {
        return taskService.getTasks(page, size);
    }

    @GetMapping("/task/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskResponse getTaskById(@PathVariable @Positive Long id) {
        return taskService.getTask(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(@RequestBody @Valid TaskRequest taskRequest) {
        return taskService.createTask(taskRequest);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskResponse updateTask(@RequestBody @Valid TaskRequest taskRequest, @PathVariable @Positive Long id) {
        return taskService.updateTask(taskRequest, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable @Positive Long id) {
        taskService.deleteTask(id);
    }

    @PutMapping(("/status/{id}"))
    @ResponseStatus(HttpStatus.OK)
    public TaskResponse changeStatusTask(@RequestBody @Valid TaskChangeStatusRequest taskChangeStatusRequest, @PathVariable @Positive Long id) {
        if (taskChangeStatusRequest.status() == TaskStatus.COMPLETED) {
            taskCounter.increment();
        }
        return taskService.changeStatus(taskChangeStatusRequest, id);
    }
}
