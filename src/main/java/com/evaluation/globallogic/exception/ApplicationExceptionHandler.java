package com.evaluation.globallogic.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@ControllerAdvice
@RestControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {

    @Getter
    @RequiredArgsConstructor
    private static class ErrorResponse {
        private final long timestamp;
        private final int codigo;
        private final String detail;
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    protected ResponseEntity<Collection<ErrorResponse>> handleConflict(ConstraintViolationException ex) {
        val now = Instant.now().toEpochMilli();
        val errors = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage() + ". current value: " + violation.getInvalidValue())
                .map(message -> new ErrorResponse(now, 400, message))
                .collect(Collectors.toSet());
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(value = UserAlreadyExistsException.class)
    protected ResponseEntity<Collection<ErrorResponse>> handleConflict(UserAlreadyExistsException ex) {
        return ResponseEntity.badRequest().body(Collections.singleton(new ErrorResponse(Instant.now().toEpochMilli(), 400, ex.getMessage())));
    }

    @ExceptionHandler({ AuthenticationException.class })
    protected ResponseEntity<Collection<ErrorResponse>> handleConflict(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singleton(new ErrorResponse(Instant.now().toEpochMilli(), 401, ex.getMessage())));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        val now = Instant.now().toEpochMilli();
        val errors = e.getBindingResult().getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .map(message -> new ErrorResponse(now, 400, message))
                .collect(Collectors.toSet());
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(value = Exception.class)
    protected ResponseEntity<Collection<ErrorResponse>> handleConflict(Exception ex) {
        return ResponseEntity.internalServerError().body(Collections.singleton(new ErrorResponse(Instant.now().toEpochMilli(), 500, ex.getMessage())));
    }
}
