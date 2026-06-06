package com.revcart.product_service.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.revcart.product_service.dto.ErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.Arrays;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public final ResponseEntity<ErrorResponse> handleProductNotFoundException(Exception ex, WebRequest request) throws Exception {
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(InvalidStatusException.class)
    public final ResponseEntity<ErrorResponse> handleInvalidStatusException(Exception ex, WebRequest request) throws Exception {
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public final ResponseEntity<ErrorResponse> handleAccessDeniedException(Exception ex, WebRequest request) throws Exception {
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ErrorResponse errorResponse;
        if (ex.getCause() instanceof InvalidFormatException ife && ife.getTargetType().isEnum()) {
            String invalidValue = ife.getValue().toString();
            String allowedValues = Arrays.toString(ife.getTargetType().getEnumConstants());
            errorResponse = new ErrorResponse(LocalDateTime.now(), "Invalid Request Payload", String.format("Value '%s' is rejected. Allowed values are: %s", invalidValue, allowedValues));
        } else {
            errorResponse = new ErrorResponse(LocalDateTime.now(), String.format("Total Errors: %s - First error: %s", ex.getErrorCount(), ((FieldError) ex.getAllErrors().getFirst()).getField() + " " + ex.getAllErrors().getFirst().getDefaultMessage()), request.getDescription(false));
        }
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
