package com.sportevents.api.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleValidationExceptions_shouldReturnBadRequest() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.emptyList());
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleValidationExceptions(ex);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody().message()).isEqualTo("Validation failed");
        assertThat(response.getBody().fieldErrors()).isEmpty();
    }


    @Test
    void handleIllegalArgumentException_shouldReturnBadRequest() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleIllegalArgumentException(ex);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody().message()).isEqualTo("Error: Invalid argument");
    }

    @Test
    void handleIllegalStateException_shouldReturnBadRequest() {
        IllegalStateException ex = new IllegalStateException("Invalid state transition");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleIllegalStateException(ex);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody().message()).isEqualTo("Error: Invalid state transition");
    }

    @Test
    void handleResourceNotFoundException_shouldReturnNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Resource not found");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleResourceNotFoundException(ex);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        assertThat(response.getBody().message()).isEqualTo("Error: Resource not found");
    }

    @Test
    void handleTypeMismatch_shouldReturnBadRequest() {
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                "invalid", Integer.class, "id", null, new IllegalArgumentException("Invalid type"));

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleTypeMismatch(ex);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody().message()).contains("Invalid value for parameter");
    }

    @Test
    void handleGenericException_shouldReturnInternalServerError() {
        Exception ex = new Exception("Unexpected error");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleGenericException(ex);

        assertThat(response.getStatusCodeValue()).isEqualTo(500);
        assertThat(response.getBody().message()).isEqualTo("Unexpected error occurred");
    }
}
