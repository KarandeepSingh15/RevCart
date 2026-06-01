package com.revcart.auth_service.exceptions;

public class UserAlreadyExistsException
        extends RuntimeException {

    public UserAlreadyExistsException(String email) {
        super("User already exists: " + email);
    }
}
