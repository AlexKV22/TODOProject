package org.todo.todoproject;

import org.springframework.boot.SpringApplication;
import org.todo.todoproject.integrationTests.TestcontainersConfiguration;

public class TestTodoProjectApplication {

    public static void main(String[] args) {
        SpringApplication.from(TodoProjectApplication::main).with(TestcontainersConfiguration.class).run(args);
    }
}
