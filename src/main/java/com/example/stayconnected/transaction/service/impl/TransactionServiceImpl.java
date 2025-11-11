package com.example.stayconnected.transaction.service.impl;

import com.example.stayconnected.transaction.enums.TransactionStatus;
import com.example.stayconnected.transaction.enums.TransactionType;
import com.example.stayconnected.transaction.model.Transaction;
import com.example.stayconnected.transaction.repository.TransactionRepository;
import com.example.stayconnected.transaction.service.TransactionService;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.wallet.model.Wallet;
import com.example.stayconnected.web.dto.transaction.FilterTransactionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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

    @Override
    public Transaction getTransactionById(UUID id) {
        return this.transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction with id " + id + " not found"));
    }

    @Override
    public List<Transaction> getLastThreeTransactions(Wallet wallet) {

        List<Transaction> lastThreeTransactions =
                this.transactionRepository.findAllBySenderOrReceiverOrderByCreatedOnDesc(wallet.getId().toString(), wallet.getId().toString())
                .stream()
                .filter(transaction -> transaction.getStatus() == TransactionStatus.SUCCEEDED)
                .limit(3)
                .toList();

        return lastThreeTransactions;
    }

    @Override
    public List<Transaction> getTransactionsByUserId(UUID userId) {
        return this.transactionRepository.findAllByOwner_IdOrderByCreatedOnDesc(userId);
    }

    @Override
    public List<Transaction> getFilteredTransactions(UUID userId, FilterTransactionRequest request) {

        String typeStr = request.getTransactionType();
        String statusStr = request.getTransactionStatus();

        boolean typeFilter = typeStr != null && !typeStr.equals("ALL");
        boolean statusFilter = statusStr != null && !statusStr.equals("ALL");

        if (!typeFilter && !statusFilter) {
            return this.transactionRepository.findAllByOwner_IdOrderByCreatedOnDesc(userId);
        }

        if (typeFilter && statusFilter) {
            TransactionType transactionType = TransactionType.valueOf(typeStr);
            TransactionStatus transactionStatus = TransactionStatus.valueOf(statusStr);
            return this.transactionRepository.findAllByStatusAndTypeAndOwner_IdOrderByCreatedOnDesc(
                    transactionStatus, transactionType, userId
            );
        }

        if (typeFilter) {
            TransactionType transactionType = TransactionType.valueOf(typeStr);
            return this.transactionRepository.findAllByTypeAndOwner_IdOrderByCreatedOnDesc(transactionType, userId);
        }

        TransactionStatus transactionStatus = TransactionStatus.valueOf(statusStr);
        return this.transactionRepository.findAllByStatusAndOwner_IdOrderByCreatedOnDesc(transactionStatus, userId);
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return this.transactionRepository.findAll();
    }


    @Override
    public BigDecimal getTotalRevenue() {

        BigDecimal totalRevenue = this.getAllTransactions()
                .stream()
                .filter(transaction -> transaction.getStatus() == TransactionStatus.SUCCEEDED)
                .filter(transaction -> transaction.getType() == TransactionType.DEPOSIT
                        ||  transaction.getType() == TransactionType.BOOKING_PAYMENT)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalRevenue;
    }

    @Override
    public List<Transaction> getAllFailedTransactions() {
        return this.transactionRepository.findAllByStatus(TransactionStatus.FAILED);
    }

    @Override
    public BigDecimal getAverageTransactionAmount() {

        BigDecimal totalRevenue = this.getTotalRevenue();

        long transactionsCount = this.transactionRepository.countAllByStatusAndTypeIn(
                TransactionStatus.SUCCEEDED,
                List.of(TransactionType.DEPOSIT,
                        TransactionType.BOOKING_PAYMENT)
        );

        if (transactionsCount == 0) {
            return BigDecimal.ZERO;
        }

        return totalRevenue.divide(BigDecimal.valueOf(transactionsCount), 2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateWeeklyAverageTransactionAmount(LocalDateTime createdAfter, LocalDateTime createdBefore) {

        BigDecimal averageTransaction = this.transactionRepository.getAverageTransactionAmountByStatusAndTypeInAndCreatedOnBetween(
                TransactionStatus.SUCCEEDED,
                List.of(TransactionType.DEPOSIT, TransactionType.BOOKING_PAYMENT),
                createdAfter,
                createdBefore
        );

        if (averageTransaction == null) return BigDecimal.ZERO;

        return averageTransaction;
    }

}
