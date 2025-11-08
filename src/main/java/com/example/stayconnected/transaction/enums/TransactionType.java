package com.example.stayconnected.transaction.enums;

public enum TransactionType {
    // When user tops up wallet -> adds balance to user's wallet
    DEPOSIT("Deposit"),
    // When user books a property and pays for it -> deducts the booking amount from user's wallet
    BOOKING_PAYMENT("Booking Payment"),
    // When user cancel booking -> returns the booking amount to user's wallet
    REFUND("Refund");

    private String displayName;

    TransactionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
