package com.example.stayconnected.wallet.service.impl;

import com.example.stayconnected.transaction.enums.TransactionStatus;
import com.example.stayconnected.transaction.enums.TransactionType;
import com.example.stayconnected.transaction.model.Transaction;
import com.example.stayconnected.transaction.service.TransactionService;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.utils.exception.WalletDoesNotExist;
import com.example.stayconnected.wallet.model.Wallet;
import com.example.stayconnected.wallet.repository.WalletRepository;
import com.example.stayconnected.wallet.service.WalletService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class WalletServiceImpl implements WalletService {

    private static final String STAY_CONNECTED = "STAY_CONNECTED";
    private static final String TOP_UP_FORMAT_DESCRIPTION = "Top up €%.2f";
    private static final String BOOKING_PAYMENT_FORMAT_DESCRIPTION = "Booking Payment %.2f";
    private static final String REFUND_FORMAT_DESCRIPTION = "Refund %.2f";;

    private final WalletRepository walletRepository;

    private final TransactionService transactionService;

    @Autowired
    public WalletServiceImpl(WalletRepository walletRepository, TransactionService transactionService) {
        this.walletRepository = walletRepository;
        this.transactionService = transactionService;
    }

    @Override
    @Transactional
    public Transaction topUp(UUID walletId, BigDecimal amount) {
        Wallet wallet = this.walletRepository.findById(walletId)
                .orElseThrow(
                        () -> new WalletDoesNotExist("Wallet with such id [%s] does not exist"
                                .formatted(walletId))
                );

        wallet.setBalance(wallet.getBalance().add(amount));

        log.info("Successfully topped-up %.2f to wallet with id [%s]. Current balance: %.2f"
                .formatted(amount, wallet.getId(), wallet.getBalance()));

        this.walletRepository.save(wallet);

        Transaction transaction = this.transactionService.persistTransaction(
                wallet.getOwner(),
                STAY_CONNECTED,
                wallet.getId().toString(),
                amount,
                wallet.getBalance(),
                TransactionType.DEPOSIT,
                TransactionStatus.SUCCEEDED,
                TOP_UP_FORMAT_DESCRIPTION.formatted(amount),
                null
        );

        return transaction;
    }

    @Override
    public Wallet createWallet(User user) {
        Wallet wallet = new Wallet(BigDecimal.valueOf(50), user);

        this.walletRepository.save(wallet);

        log.info("Successfully created a wallet with id [%s] for user id [%s]"
                .formatted(wallet.getId(), user.getId()));

        return wallet;
    }

    @Override
    public List<Transaction> getLastThreeTransactions(Wallet wallet) {
        return this.transactionService.getLastThreeTransactions(wallet);
    }
}
