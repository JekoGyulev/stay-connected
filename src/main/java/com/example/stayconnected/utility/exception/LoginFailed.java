package com.example.stayconnected.utility.exception;

public class LoginFailed extends RuntimeException {
    public LoginFailed(String message) {
        super(message);
    }
}
