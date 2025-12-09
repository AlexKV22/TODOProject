package org.todo.todoproject.exception;

public class NotIDWhenSaveTaskException extends RuntimeException {

    public NotIDWhenSaveTaskException() {
        super("ID сохраненного обьекта null, ошибка сохранения");

    }
}
