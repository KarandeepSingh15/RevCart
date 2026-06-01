package com.revcart.auth_service.exceptions;

public class InvalidCredentialException
        extends RuntimeException {

    public InvalidCredentialException() {
        super("Invalid credentials");
    }
}