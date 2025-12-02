package com.example.stayconnected.wallet.service;

import com.example.stayconnected.reservation.client.dto.CreateReservationRequest;
import com.example.stayconnected.transaction.enums.TransactionType;
import com.example.stayconnected.transaction.model.Transaction;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.wallet.model.Wallet;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface WalletService {

    Transaction topUp(UUID walletId, BigDecimal amount, TransactionType transactionType);

    Wallet createWallet(User user);

    List<Transaction> getLastThreeTransactions(Wallet wallet);

    void exchange(CreateReservationRequest createReservationRequest, UUID ownerId);

    // Refund method -> when user cancels reservation
}
