package com.example.stayconnected.utils.exception;

public class UsernameAlreadyExists extends RuntimeException {
    public UsernameAlreadyExists(String message) {
        super(message);
    }
}
