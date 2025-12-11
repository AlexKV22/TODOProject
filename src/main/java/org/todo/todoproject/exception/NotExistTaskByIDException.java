package org.todo.todoproject.exception;

public class NotExistTaskByIDException extends RuntimeException {
    private final Long id;

    public NotExistTaskByIDException(Long id) {
        super("Task does not exist with id: " + id);
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
