package com.example.stayconnected.transaction.enums;

public enum TransactionType {
    DEPOSIT("Deposit"),
    BOOKING_PAYMENT("Booking Payment"),
    REFUND("Refund"),
    BOOKING_EARNING("Booking Earning"),
    EARNING_REVERSAL("Earning Reversal");

    private String displayName;

    TransactionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
