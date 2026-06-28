package com.revcart.cart_service.exception;

import com.revcart.cart_service.dto.ErrorResponse;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
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

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler({ProductServiceUnavailableException.class, CallNotPermittedException.class, ValidationException.class, ProductInactiveException.class})
    public final ResponseEntity<ErrorResponse> handleProductServiceUnavailableException(Exception ex, WebRequest request) throws Exception {
        ErrorResponse errorResponse =new ErrorResponse(LocalDateTime.now(),ex.getMessage(),request.getDescription(false));
        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler({ProductNotAvailableException.class, CartItemNotFoundException.class})
    public final ResponseEntity<ErrorResponse> handleNotFoundException(Exception ex, WebRequest request) throws Exception {
        ErrorResponse errorResponse =new ErrorResponse(LocalDateTime.now(),ex.getMessage(),request.getDescription(false));
        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(CartOwnershipException.class)
    public final ResponseEntity<ErrorResponse> handleCartOwnershipException(Exception ex, WebRequest request) throws Exception {
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ErrorResponse errorResponse =new ErrorResponse(LocalDateTime.now(),String.format("Total Errors: %s - First error: %s",ex.getErrorCount(),((FieldError) ex.getAllErrors().getFirst()).getField()+" "+ex.getAllErrors().getFirst().getDefaultMessage()),request.getDescription(false));
        return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
    }
}



