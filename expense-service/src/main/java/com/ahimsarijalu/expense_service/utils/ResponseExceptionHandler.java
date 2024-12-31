package com.ahimsarijalu.expense_service.utils;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ResponseExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public <T> ResponseEntity<ApiResponse<T>> handleEntityNotFound(EntityNotFoundException e, WebRequest request) {
        ApiResponse<T> response = new ApiResponse<>(
                HttpStatus.NOT_FOUND.value(),
                false,
                "Expense service: " + e.getMessage(),
                null
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<ApiResponse<List<String>>> handleBadRequest(Exception e, WebRequest request) {
        String errorMessage = "";
        List<String> errorDetails = null;

        if (e instanceof MethodArgumentNotValidException) {
            errorDetails = ((MethodArgumentNotValidException) e)
                    .getBindingResult()
                    .getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            errorMessage = "Expense service: " + "Validation error(s) occurred.";
        } else if (e instanceof IllegalArgumentException) {
            errorMessage = e.getMessage();
        }

        ApiResponse<List<String>> response = new ApiResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                false,
                "Expense service: " + errorMessage,
                errorDetails
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public <T> ResponseEntity<ApiResponse<T>> handleResponseStatusException(ResponseStatusException e) {
        ApiResponse<T> response = new ApiResponse<>(
                e.getStatusCode().value(),
                false,
                "Expense service: " + e.getReason(),
                null
        );
        return new ResponseEntity<>(response, e.getStatusCode());
    }

    @ExceptionHandler(Exception.class)
    public <T> ResponseEntity<ApiResponse<T>> handleGlobalException(Exception e) {
        ApiResponse<T> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                false,
                "Expense service: " + "An unexpected error occurred: " + e.getMessage(),
                null
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
