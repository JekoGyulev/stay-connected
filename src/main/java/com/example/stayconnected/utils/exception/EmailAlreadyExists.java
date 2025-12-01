package com.example.stayconnected.utils.exception;

public class EmailAlreadyExists extends RuntimeException {
    public EmailAlreadyExists(String message) {
        super(message);
    }
}
