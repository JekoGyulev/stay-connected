package com.example.stayconnected.wallet.service;

import com.example.stayconnected.user.model.User;
import com.example.stayconnected.wallet.model.Wallet;

import java.math.BigDecimal;
import java.util.UUID;

public interface WalletService {

    void topUp(UUID walletId, BigDecimal amount);

    Wallet createWallet(User user);
}
