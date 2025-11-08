package com.example.stayconnected.transaction.service;

import com.example.stayconnected.transaction.enums.TransactionStatus;
import com.example.stayconnected.transaction.enums.TransactionType;
import com.example.stayconnected.transaction.model.Transaction;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.wallet.model.Wallet;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface TransactionService {
    Transaction persistTransaction(User owner, String sender,
                                   String receiver, BigDecimal amount,
                                   BigDecimal balanceLeft, TransactionType transactionType,
                                   TransactionStatus transactionStatus,
                                   String description, String reasonForFailure);

    Transaction getTransactionById(UUID id);

    List<Transaction> getLastThreeTransactions(Wallet wallet);

    List<Transaction> getTransactionsByUserId(UUID userId);
}
