package com.example.stayconnected.utility.exception;

public class WalletDoesNotExist extends RuntimeException {
    public WalletDoesNotExist(String message) {
        super(message);
    }
}
