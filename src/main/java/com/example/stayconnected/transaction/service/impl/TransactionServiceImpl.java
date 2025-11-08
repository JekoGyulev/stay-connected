package com.example.stayconnected.transaction.service.impl;

import com.example.stayconnected.transaction.enums.TransactionStatus;
import com.example.stayconnected.transaction.enums.TransactionType;
import com.example.stayconnected.transaction.model.Transaction;
import com.example.stayconnected.transaction.repository.TransactionRepository;
import com.example.stayconnected.transaction.service.TransactionService;
import com.example.stayconnected.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Transaction persistTransaction(User owner, String sender, String receiver, BigDecimal amount, BigDecimal balanceLeft, TransactionType transactionType, TransactionStatus transactionStatus, String description, String reasonForFailure) {
        Transaction transaction = new Transaction(
                owner, sender,
                receiver, amount,
                balanceLeft, transactionType,
                transactionStatus, description,
                reasonForFailure
        );

        return this.transactionRepository.save(transaction);
    }
}
