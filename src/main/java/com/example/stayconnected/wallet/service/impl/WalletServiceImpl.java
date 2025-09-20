package com.example.stayconnected.wallet.service.impl;

import com.example.stayconnected.user.model.User;
import com.example.stayconnected.utility.exception.WalletDoesNotExist;
import com.example.stayconnected.wallet.model.Wallet;
import com.example.stayconnected.wallet.repository.WalletRepository;
import com.example.stayconnected.wallet.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    @Autowired
    public WalletServiceImpl(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Override
    public void topUp(UUID walletId, BigDecimal amount) {
        Wallet wallet = this.walletRepository.findById(walletId)
                .orElseThrow(
                        () -> new WalletDoesNotExist("Wallet with such id [%s] does not exist"
                                .formatted(walletId))
                );

        wallet.setBalance(wallet.getBalance().add(amount));

        log.info("Successfully topped-up %.2f to wallet with id [%s]. Current balance: %.2f"
                .formatted(amount, wallet.getId(), wallet.getBalance()));

        this.walletRepository.save(wallet);
    }

    @Override
    public Wallet createWallet(User user) {
        Wallet wallet = new Wallet(BigDecimal.valueOf(50), user);

        this.walletRepository.save(wallet);

        log.info("Successfully created a wallet with id [%s] for user id [%s]"
                .formatted(wallet.getId(), user.getId()));

        return wallet;
    }
}
