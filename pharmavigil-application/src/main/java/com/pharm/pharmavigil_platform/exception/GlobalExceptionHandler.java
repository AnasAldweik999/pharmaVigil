package com.pharm.pharmavigil_platform.exception;

import com.pharm.pharmavigil_platform.validators.exceptions.SystemViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    record ErrorResponse(int status, String message, Instant timestamp) {}

    record ViolationResponse(String violator, String violation) {}

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(404, ex.getMessage(), Instant.now()));
    }

    @ExceptionHandler(SystemViolationException.class)
    public ResponseEntity<List<ViolationResponse>> handleSystemViolation(SystemViolationException ex) {
        List<ViolationResponse> responses = ex.getViolations().stream()
                .map(v -> new ViolationResponse(v.field(), v.message()))
                .toList();
        log.info("System violation", ex);
        return ResponseEntity.badRequest().body(responses);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ViolationResponse> handleValidation(MethodArgumentNotValidException ex) {
        FieldError first = ex.getBindingResult().getFieldErrors().get(0);
        return ResponseEntity.badRequest()
                .body(new ViolationResponse(first.getField(), first.getDefaultMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(403, "Access denied", Instant.now()));
    }
}
