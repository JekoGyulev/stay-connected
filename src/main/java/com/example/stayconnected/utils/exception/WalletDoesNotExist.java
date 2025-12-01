package com.example.stayconnected.utils.exception;

public class WalletDoesNotExist extends RuntimeException {
    public WalletDoesNotExist(String message) {
        super(message);
    }
}
