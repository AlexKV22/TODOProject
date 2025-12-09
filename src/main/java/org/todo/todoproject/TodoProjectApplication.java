package org.todo.todoproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TodoProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(TodoProjectApplication.class, args);
    }

}
