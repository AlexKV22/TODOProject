package org.todo.todoproject.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.todo.todoproject.controller.TaskController;
import org.todo.todoproject.dto.request.TaskChangeStatusRequest;
import org.todo.todoproject.dto.request.TaskRequest;
import org.todo.todoproject.dto.response.TaskResponse;
import org.todo.todoproject.service.TaskServiceImpl;
import org.todo.todoproject.util.TaskStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@DisplayName(value = "Тесты контроллера")
class TaskControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskServiceImpl taskService;

    static Stream<Arguments> getAllTasksInvalidScenarios() {
        return Stream.of(
                Arguments.of("-100", "-100", 400),
                Arguments.of("text", "text", 400),
                Arguments.of("3.14", "6.78", 400)
        );
    }

    @ParameterizedTest
    @MethodSource("getAllTasksInvalidScenarios")
    @DisplayName(value = "Невалидные тесты получения всех задач")
    void getAllTasksInvalidTest(String limit, String offset, int expectedStatus) throws Exception {
        mockMvc.perform(get("/").param("limit", limit).param("offset", offset)).andExpect(status().is(expectedStatus));
        Mockito.verify(taskService, Mockito.never()).getTasks(anyInt(), anyInt());
    }

    @Test
    @DisplayName(value = "Тест отсутствия в базе всех задач")
    void getAllTasksWhenNotExistTasksTest() throws Exception {
        List<TaskResponse> taskResponses = Collections.emptyList();

        Mockito.when(taskService.getTasks(5, 0)).thenReturn(taskResponses);
        mockMvc.perform(get("/").param("limit", "5").param("offset", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        Mockito.verify(taskService, Mockito.times(1)).getTasks(5, 0);
    }

    @Test
    @DisplayName(value = "Валидные тесты получения всех задач")
    void getAllTasksValidTest() throws Exception {
        TaskResponse taskResponseOne = new TaskResponse(1L, "Create task", Instant.parse("2025-12-07T14:14:45.622332Z"), LocalDate.parse("2025-12-07"), TaskStatus.CREATED);
        TaskResponse taskResponseTwo = new TaskResponse(2L, "Enouth task", Instant.parse("2025-12-07T10:06:26.086596Z"), null, TaskStatus.CREATED);
        List<TaskResponse> taskResponses = List.of(taskResponseOne, taskResponseTwo);

        Mockito.when(taskService.getTasks(5, 0)).thenReturn(taskResponses);

        ResultActions resultActions = mockMvc.perform(get("/").param("limit", "5").param("offset", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("[0].id").value(1))
                .andExpect(jsonPath("[0].title").value("Create task"))
                .andExpect(jsonPath("[1].id").value(2))
                .andExpect(jsonPath("[1].title").value("Enouth task"));

        String response = """
          [
               {
                   "id": 1,
                   "title": "Create task",
                   "createAt": "2025-12-07T14:14:45.622332Z",
                   "expireAt": "2025-12-07",
                   "status": "CREATED"
               },
               {
                   "id": 2,
                   "title": "Enouth task",
                   "createAt": "2025-12-07T10:06:26.086596Z",
                   "expireAt": null,
                   "status": "CREATED"
               }
           ]
        """;
        resultActions.andExpect(result -> JSONAssert.assertEquals(response, result.getResponse().getContentAsString(), false));
        Mockito.verify(taskService, Mockito.times(1)).getTasks(5, 0);
    }


    static Stream<Arguments> getTaskByIdInvalidScenarios() {
        return Stream.of(
                Arguments.of("-100", 400),
                Arguments.of("text", 400),
                Arguments.of("3.14", 400)
        );
    }

    @ParameterizedTest
    @MethodSource("getTaskByIdInvalidScenarios")
    @DisplayName(value = "Невалидные тесты получения задачи по айди")
    void getTaskByIdInvalidTest(String id, int expectedStatus) throws Exception {
        mockMvc.perform(get("/task/{id}", id)).andExpect(status().is(expectedStatus));
        Mockito.verify(taskService, Mockito.never()).getTask(any());
    }

    @Test
    @DisplayName(value = "Валидные тесты получения задачи по айди")
    void getTaskByIdValidTest() throws Exception {
        TaskResponse taskResponseOne = new TaskResponse(1L, "Create task", Instant.parse("2025-12-07T14:14:45.622332Z"), LocalDate.parse("2025-12-07"), TaskStatus.CREATED);

        Mockito.when(taskService.getTask(1L)).thenReturn(taskResponseOne);

        ResultActions resultFind = mockMvc.perform(get("/task/{id}", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Create task"));

        String response = """
               {
                   "id": 1,
                   "title": "Create task",
                   "createAt": "2025-12-07T14:14:45.622332Z",
                   "expireAt": "2025-12-07",
                   "status": "CREATED"
               }
        """;
        resultFind.andExpect(result -> JSONAssert.assertEquals(response, result.getResponse().getContentAsString(), false));

        Mockito.verify(taskService, Mockito.times(1)).getTask(1L);
    }

    @Test
    @DisplayName(value = "Тесты отсутствия задачи по айди")
    void getTaskByIdWithNotExistTest() throws Exception {
        Mockito.when(taskService.getTask(4L)).thenReturn(null);
        mockMvc.perform(get("/task/{id}", "4")).andExpect(status().isNotFound());
        Mockito.verify(taskService, Mockito.times(1)).getTask(4L);
    }

    static Stream<Arguments> createTaskInvalidScenarios() {
        return Stream.of(
                Arguments.of("{\"title\":\"\", \"expireAt\":\"2025-12-07\"}", 400),
                Arguments.of("{\"title\":\"task1\", \"expireAt\":\"2025-12-05\"}", 400),
                Arguments.of("{\"title\":\"task1\", \"expireAt\":\"rtt\"}", 400)
        );
    }

    @ParameterizedTest
    @MethodSource("createTaskInvalidScenarios")
    @DisplayName(value = "Невалидные тесты создания задачи")
    void createTaskInvalidTest(String jsonRequest, int expectedStatus) throws Exception {
        mockMvc.perform(post("/").contentType(MediaType.APPLICATION_JSON).content(jsonRequest)).andExpect(status().is(expectedStatus));
        Mockito.verify(taskService, Mockito.never()).createTask(any());
    }

    @Test
    @DisplayName(value = "Валидные тесты создания задачи")
    void createTaskValidTest() throws Exception {
        TaskRequest taskRequest = new TaskRequest("Create task", LocalDate.now());
        TaskResponse taskResponseOne = new TaskResponse(1L, "Create task", Instant.parse("2025-12-07T14:14:45.622332Z"), LocalDate.parse("2025-12-07"), TaskStatus.CREATED);

        Mockito.when(taskService.createTask(taskRequest)).thenReturn(taskResponseOne);

        ResultActions resultCreate = mockMvc.perform(post("/").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Create task"));

        String response = """
               {
                   "id": 1,
                   "title": "Create task",
                   "createAt": "2025-12-07T14:14:45.622332Z",
                   "expireAt": "2025-12-07",
                   "status": "CREATED"
               }
        """;
        resultCreate.andExpect(result -> JSONAssert.assertEquals(response, result.getResponse().getContentAsString(), false));

        Mockito.verify(taskService, Mockito.times(1)).createTask(taskRequest);
    }

    static Stream<Arguments> updateTaskInvalidScenarios() {
        return Stream.of(
                Arguments.of("{\"title\":\"task1\", \"expireAt\":\"2025-12-07\"}", "-100", 400),
                Arguments.of("{\"title\":\"task1\", \"expireAt\":\"2025-12-07\"}", "text", 400),
                Arguments.of("{\"title\":\"task1\", \"expireAt\":\"2025-12-07\"}", "4.17", 400),
                Arguments.of("{\"title\":\"\", \"expireAt\":\"2025-12-07\"}", "1", 400),
                Arguments.of("{\"title\":\"task1\", \"expireAt\":\"2025-12-05\"}", "1", 400),
                Arguments.of("{\"title\":\"task1\", \"expireAt\":\"rtt\"}", "1", 400)
        );
    }

    @ParameterizedTest
    @MethodSource("updateTaskInvalidScenarios")
    @DisplayName(value = "Невалидные тесты обновления задачи")
    void updateTaskInvalidTest(String jsonRequest, String id, int expectedStatus) throws Exception {
        mockMvc.perform(put("/{id}", id).contentType(MediaType.APPLICATION_JSON).content(jsonRequest)).andExpect(status().is(expectedStatus));
        Mockito.verify(taskService, Mockito.never()).updateTask(any(), any());
    }

    static Stream<Arguments> updateTaskValidScenarios() {
        return Stream.of(
                Arguments.of("{\"title\":\"task1\", \"expireAt\":\"2025-12-08\"}", "1", 200),
                Arguments.of("{\"title\":\"task1\", \"expireAt\":\"\"}", "1", 200)
        );
    }

    @ParameterizedTest
    @MethodSource("updateTaskValidScenarios")
    @DisplayName(value = "Валидные тесты обновления задачи")
    void updateTaskValidTest(String jsonRequest, String id, int expectedStatus) throws Exception {
        TaskResponse taskResponseOne = new TaskResponse(1L, "Modify task", Instant.parse("2025-12-07T14:14:45.622332Z"), LocalDate.parse("2025-12-07"), TaskStatus.UPDATED);

        Mockito.when(taskService.updateTask(Mockito.any(TaskRequest.class), Mockito.any(Long.class))).thenReturn(taskResponseOne);

        ResultActions resultUpdate = mockMvc.perform(put("/{id}", id).contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
                .andExpect(status().is(expectedStatus))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Modify task"));

        String response = """
               {
                   "id": 1,
                   "title": "Modify task",
                   "createAt": "2025-12-07T14:14:45.622332Z",
                   "expireAt": "2025-12-07",
                   "status": "UPDATED"
               }
        """;
        resultUpdate.andExpect(result -> JSONAssert.assertEquals(response, result.getResponse().getContentAsString(), false));

        Mockito.verify(taskService, Mockito.times(1)).updateTask(Mockito.any(TaskRequest.class), Mockito.any(Long.class));
    }

    @Test
    @DisplayName(value = "Тесты отсутствия задачи для обновления")
    void updateTaskWithNotExistTest() throws Exception {
        TaskRequest taskRequest = new TaskRequest("Modify task", LocalDate.now());
        Mockito.when(taskService.updateTask(any(TaskRequest.class), any(Long.class))).thenReturn(null);
        mockMvc.perform(put("/{id}", "4").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isNotFound());

        Mockito.verify(taskService, Mockito.times(1)).updateTask(any(TaskRequest.class), any(Long.class));
    }

    static Stream<Arguments> deleteTaskInvalidScenarios() {
        return Stream.of(
                Arguments.of("-100", 400),
                Arguments.of("text", 400),
                Arguments.of("3.14", 400)
        );
    }

    @ParameterizedTest
    @MethodSource("deleteTaskInvalidScenarios")
    @DisplayName(value = "Невалидные тесты удаления задачи")
    void deleteTaskInvalidTest(String id, int expectedStatus) throws Exception {
        mockMvc.perform(delete("/{id}", id)).andExpect(status().is(expectedStatus));
        Mockito.verify(taskService, Mockito.never()).deleteTask(any());
    }

    @Test
    @DisplayName(value = "Валидные тесты удаления задачи")
    void deleteTaskValidTest() throws Exception {
        Mockito.when(taskService.deleteTask(any(Long.class))).thenReturn(true);

        mockMvc.perform(delete("/{id}", "4")).andExpect(status().isNoContent());

        Mockito.verify(taskService, Mockito.times(1)).deleteTask(any(Long.class));
    }

    @Test
    @DisplayName(value = "Тесты отсутствия задачи для удаления")
    void deleteTaskWithNotExistTest() throws Exception {
        Mockito.when(taskService.deleteTask(any(Long.class))).thenReturn(false);

        mockMvc.perform(delete("/{id}", "4")).andExpect(status().isNotFound());

        Mockito.verify(taskService, Mockito.times(1)).deleteTask(any(Long.class));
    }

    static Stream<Arguments> changeStatusTaskInvalidScenarios() {
        return Stream.of(
                Arguments.of("{\"status\":\"CREATED\"}", "-100", 400),
                Arguments.of("{\"status\":\"CREATED\"}", "text", 400),
                Arguments.of("{\"status\":\"CREATED\"}", "3.14", 400),
                Arguments.of("{\"status\":\"created\"}", "1", 400),
                Arguments.of("{\"status\":\"fweee\"}", "1", 400),
                Arguments.of("{\"status\":\"456\"}", "1", 400)
        );
    }

    @ParameterizedTest
    @MethodSource("changeStatusTaskInvalidScenarios")
    @DisplayName(value = "Невалидные тесты изменения статуса задачи")
    void changeStatusTaskInvalidTest(String jsonRequest, String id, int expectedStatus) throws Exception {
        mockMvc.perform(put("/status/{id}", id).contentType(MediaType.APPLICATION_JSON).content(jsonRequest)).andExpect(status().is(expectedStatus));
        Mockito.verify(taskService, Mockito.never()).changeStatus(any(), any());
    }

    @Test
    @DisplayName(value = "Валидные тесты изменения статуса задачи")
    void changeStatusTaskValidTest() throws Exception {
        TaskChangeStatusRequest taskChangeStatusRequest = new TaskChangeStatusRequest(TaskStatus.EXPIRED);
        TaskResponse taskResponseOne = new TaskResponse(1L, "Modify task", Instant.parse("2025-12-07T14:14:45.622332Z"), LocalDate.parse("2025-12-07"), TaskStatus.EXPIRED);

        Mockito.when(taskService.changeStatus(any(TaskChangeStatusRequest.class), any(Long.class))).thenReturn(taskResponseOne);

        ResultActions resultChangeStatus = mockMvc.perform(put("/status/{id}", "1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(taskChangeStatusRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Modify task"));

        String response = """
               {
                   "id": 1,
                   "title": "Modify task",
                   "createAt": "2025-12-07T14:14:45.622332Z",
                   "expireAt": "2025-12-07",
                   "status": "EXPIRED"
               }
        """;
        resultChangeStatus.andExpect(result -> JSONAssert.assertEquals(response, result.getResponse().getContentAsString(), false));

        Mockito.verify(taskService, Mockito.times(1)).changeStatus(any(TaskChangeStatusRequest.class), any(Long.class));
    }

    @Test
    @DisplayName(value = "Тесты  отсутствия задачи для изменения статуса")
    void changeStatusTaskWithNotExistTest() throws Exception {
        TaskChangeStatusRequest taskChangeStatusRequest = new TaskChangeStatusRequest(TaskStatus.EXPIRED);

        Mockito.when(taskService.changeStatus(any(TaskChangeStatusRequest.class), any(Long.class))).thenReturn(null);

        mockMvc.perform(put("/status/{id}", "4").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(taskChangeStatusRequest)))
                .andExpect(status().isNotFound());

        Mockito.verify(taskService, Mockito.times(1)).changeStatus(any(TaskChangeStatusRequest.class), any(Long.class));
    }
}
