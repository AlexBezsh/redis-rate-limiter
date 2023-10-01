package com.alexbezsh.redis.ratelimiter.aspect;

import com.alexbezsh.redis.ratelimiter.exception.RateLimitException;
import com.alexbezsh.redis.ratelimiter.model.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.toCollection(ArrayList::new));
        e.getBindingResult().getGlobalErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .forEach(errors::add);
        return badRequest(errors);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(ConstraintViolationException e) {
        List<String> errors = e.getConstraintViolations().stream()
            .map(v -> v.getPropertyPath() + ": " + v.getMessage()).toList();
        return badRequest(errors);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public ErrorResponse handle(RateLimitException e) {
        return new ErrorResponse(HttpStatus.TOO_MANY_REQUESTS, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handle(Exception e) {
        String message = "Unexpected error. Reason: " + e.getMessage();
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    private ErrorResponse badRequest(List<String> errors) {
        String message = String.join("; ", errors);
        return new ErrorResponse(HttpStatus.BAD_REQUEST, message);
    }

}
