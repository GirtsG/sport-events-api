package com.sportevents.api.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String ERROR_PREFIX = "Error: ";

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.warn("Type mismatch error: {}", ex.getMessage());
        String message = "Invalid value for parameter '%s': %s".formatted(ex.getName(), ex.getValue());
        ErrorResponse errorResponse = new ErrorResponse(message, null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage());
        List<ErrorResponse.FieldErrorResponse> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ErrorResponse.FieldErrorResponse(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();
        ErrorResponse errorResponse = new ErrorResponse("Validation failed", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Illegal argument: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(ERROR_PREFIX + ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex) {
        log.error("Illegal state: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(ERROR_PREFIX + ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(ERROR_PREFIX + ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unhandled exception occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Unexpected error occurred", null));
    }

    public record ErrorResponse(String message, List<FieldErrorResponse> fieldErrors) {
        public record FieldErrorResponse(String field, String message) {
        }
    }
}
