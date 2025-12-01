package com.example.stayconnected.utils.exception;

public class LoginFailed extends RuntimeException {
    public LoginFailed(String message) {
        super(message);
    }
}
