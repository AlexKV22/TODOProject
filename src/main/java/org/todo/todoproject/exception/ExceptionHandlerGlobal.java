package org.todo.todoproject.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class ExceptionHandlerGlobal {
    @ExceptionHandler(MethodArgumentNotValidException.class) // ВЫПАДАЕТ ВО ВРЕМЯ ОШИБКИ ВАЛИДАЦИИ В ТЕЛЕ ЗАПРОСА
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, String> validateException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((org.springframework.validation.FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        System.out.println("СРАБОТАЛО ИСКЛЮЧЕНИЕ MethodArgumentNotValidException");
        return errors;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class) // ВЫПДААЕТ ВО ВРЕМЯ ОШИБКИ ПРИВЕДЕНИЯ ТИПОВ В ЗАПРОСЕ
    public ErrorResponse uniqueFieldException(MethodArgumentTypeMismatchException e) {
        log.error(e.getMessage());
        System.out.println("СРАБОТАЛО ИСКЛЮЧЕНИЕ MethodArgumentTypeMismatchException");
        return ErrorResponse.builder(e, ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage())).build();
    }

    @ExceptionHandler(DataAccessException.class)
    public ErrorResponse uniqueFieldException(DataAccessException e) {
        log.error(e.getMessage());
        return ErrorResponse.builder(e, ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage())).build();
    }

    @ExceptionHandler(NotIDWhenSaveTaskException.class) // ВЫПАДАЕТ КОГДА У СОХРАНЕННОЙ ТАСКИ НЕТ АЙДИ
    public ErrorResponse notIDAfterSaveException(NotIDWhenSaveTaskException e) {
        log.error(e.getMessage());
        return ErrorResponse.builder(e, ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage())).build();
    }

    @ExceptionHandler(ConstraintViolationException.class) // ВЫПАДАЕТ ВО ВРЕМЯ ОШИБКИ ВАЛИДАЦИИ ПАРАМЕТРОВ ЗАПРОСА И ПАРАМЕТРОВ ПУТИ
    public ErrorResponse uniqueFieldException(ConstraintViolationException e) {
        log.error(e.getMessage());
        System.out.println("СРАБОТАЛО ИСКЛЮЧЕНИЕ ConstraintViolationException");
        return ErrorResponse.builder(e, ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage())).build();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class) // ВЫПАДАЕТ ВО ВРЕМЯ ОШИБКИ ПАРСИНГА
    public ErrorResponse uniqueFieldException(HttpMessageNotReadableException e) {
        log.error(e.getMessage());
        System.out.println("СРАБОТАЛО ИСКЛЮЧЕНИЕ HttpMessageNotReadableException");
        return ErrorResponse.builder(e, ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage())).build();
    }
}
