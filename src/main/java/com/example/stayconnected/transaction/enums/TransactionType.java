package com.example.stayconnected.transaction.enums;

public enum TransactionType {
    // When user tops up wallet -> adds balance to user's wallet
    DEPOSIT,
    // When user books a property and pays for it -> deducts the booking amount from user's wallet
    BOOKING_PAYMENT,
    // When user cancel booking -> returns the booking amount to user's wallet
    REFUND
}
