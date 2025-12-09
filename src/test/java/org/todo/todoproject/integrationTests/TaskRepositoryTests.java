package org.todo.todoproject.integrationTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlScriptsTestExecutionListener;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.todo.todoproject.dto.request.TaskChangeStatusRequest;
import org.todo.todoproject.entity.Task;
import org.todo.todoproject.repository.TaskRepositoryImpl;
import org.todo.todoproject.util.TaskStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Import(TaskRepositoryImpl.class)
@JdbcTest
@ActiveProfiles("test")
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        SqlScriptsTestExecutionListener.class
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@DisplayName(value = "Класс тестов репозитория")
class TaskRepositoryTests {

    @Container
    static PostgreSQLContainer postgresContainer = new PostgreSQLContainer("postgres:latest").withInitScript("schemaAndTableCreate.sql");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @BeforeAll
    static void beforeAll() {
        postgresContainer.start();
    }

    @Autowired
    private TaskRepositoryImpl taskRepositoryImpl;


    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, value = "/insertTable.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/truncateTable.sql")
    @DisplayName(value = "Валидные тесты создания задачи")
    void createTaskTest() {
        Task task = Task.builder().title("Create task").createAt(Instant.now()).expireAt(LocalDate.now()).status(TaskStatus.CREATED).build();
        Task save = taskRepositoryImpl.save(task);
        Assertions.assertEquals(2, save.getId());
        Assertions.assertEquals("Create task", save.getTitle());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, value = "/insertTable.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/truncateTable.sql")
    @DisplayName(value = "Валидные тесты создания задачи")
    void createTaskWhereExpireAtIsNullTest() {
        Task task = Task.builder().title("Create task").createAt(Instant.now()).expireAt(null).status(TaskStatus.CREATED).build();
        Task save = taskRepositoryImpl.save(task);
        Assertions.assertEquals(2, save.getId());
        Assertions.assertEquals("Create task", save.getTitle());
        Assertions.assertNull(task.getExpireAt());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, value = "/insertTable.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/truncateTable.sql")
    @DisplayName(value = "Валидные тесты получения всех задач")
    void findAllTasksTest() {
        List<Task> all = taskRepositoryImpl.findAll(5, 0);
        Assertions.assertEquals(1, all.size());
        Assertions.assertEquals("Task", all.get(0).getTitle());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/truncateTable.sql")
    @DisplayName(value = "Валидные тесты получения всех задач")
    void findEmptyListTasksTest() {
        List<Task> all = taskRepositoryImpl.findAll(5, 0);
        Assertions.assertTrue(all.isEmpty());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, value = "/insertTable.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/truncateTable.sql")
    @DisplayName(value = "Валидные тесты получения задачи по айди")
    void findByIdTaskTest() {
        Task task = taskRepositoryImpl.findById(1L).get();
        Assertions.assertEquals(1, task.getId());
        Assertions.assertEquals("Task", task.getTitle());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/truncateTable.sql")
    @DisplayName(value = "Валидные тесты получения задачи по айди")
    void findByIdWithNotExistIDTaskTest() {
        Optional<Task> byId = taskRepositoryImpl.findById(1L);
        Assertions.assertTrue(byId.isEmpty());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, value = "/insertTable.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/truncateTable.sql")
    @DisplayName(value = "Валидные тесты удаления задачи")
    void deleteTaskTest() {
        boolean delete = taskRepositoryImpl.delete(1L);
        Assertions.assertTrue(delete);
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/truncateTable.sql")
    @DisplayName(value = "Валидные тесты удаления задачи")
    void deleteNotExistTaskTest() {
        boolean delete = taskRepositoryImpl.delete(1L);
        Assertions.assertFalse(delete);
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, value = "/insertTable.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/truncateTable.sql")
    @DisplayName(value = "Валидные тесты обновления задачи")
    void updateTaskTest() {
        Task task = Task.builder().id(1L).title("Modify task").createAt(Instant.now()).expireAt(LocalDate.now()).status(TaskStatus.UPDATED).build();
        Optional<Task> update = taskRepositoryImpl.update(task, 1L);
        Assertions.assertTrue(update.isPresent());
        Assertions.assertEquals("Modify task", update.get().getTitle());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/truncateTable.sql")
    @DisplayName(value = "Валидные тесты обновления задачи")
    void updateWithNotExistTaskTest() {
        Task task = Task.builder().id(1L).title("Modify task").createAt(Instant.now()).expireAt(LocalDate.now()).status(TaskStatus.UPDATED).build();
        Optional<Task> update = taskRepositoryImpl.update(task, 1L);
        Assertions.assertTrue(update.isEmpty());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, value = "/insertTable.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/truncateTable.sql")
    @DisplayName(value = "Валидные тесты изменения статуса задачи")
    void changeStatusTaskTest() {
        TaskChangeStatusRequest taskChangeStatusRequest = new TaskChangeStatusRequest(TaskStatus.EXPIRED);
        Optional<Task> changeStatus = taskRepositoryImpl.changeStatus(taskChangeStatusRequest, 1L);
        Assertions.assertTrue(changeStatus.isPresent());
        Assertions.assertEquals(TaskStatus.EXPIRED, changeStatus.get().getStatus());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/truncateTable.sql")
    @DisplayName(value = "Валидные тесты изменения статуса задачи")
    void changeStatusWithNotExistTaskTest() {
        TaskChangeStatusRequest taskChangeStatusRequest = new TaskChangeStatusRequest(TaskStatus.EXPIRED);
        Optional<Task> changeStatus = taskRepositoryImpl.changeStatus(taskChangeStatusRequest, 1L);
        Assertions.assertTrue(changeStatus.isEmpty());
    }

}
