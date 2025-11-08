package com.example.stayconnected.wallet.service;

import com.example.stayconnected.transaction.model.Transaction;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.wallet.model.Wallet;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface WalletService {

    Transaction topUp(UUID walletId, BigDecimal amount);

    Wallet createWallet(User user);

    List<Transaction> getLastThreeTransactions(Wallet wallet);
}
