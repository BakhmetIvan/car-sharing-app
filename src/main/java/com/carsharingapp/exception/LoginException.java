package com.carsharingapp.exception;

public class LoginException extends RuntimeException {
    public LoginException(String message, Throwable cause) {
        super(message, cause);
    }
}