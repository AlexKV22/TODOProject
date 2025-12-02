package org.todo.todoproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.todo.todoproject.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
}
