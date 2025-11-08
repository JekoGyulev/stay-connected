package com.example.stayconnected.transaction.enums;

public enum TransactionStatus {
    FAILED("Failed"),
    SUCCEEDED("Succeeded");

    private String displayName;

    TransactionStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
