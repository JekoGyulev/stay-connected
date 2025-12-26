package com.example.stayconnected.wallet.service.impl;

import com.example.stayconnected.aop.annotations.LogCreation;
import com.example.stayconnected.property.model.Property;
import com.example.stayconnected.property.service.PropertyService;
import com.example.stayconnected.reservation.client.dto.CreateReservationRequest;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class WalletServiceImpl implements WalletService {

    private static final String STAY_CONNECTED = "STAY_CONNECTED";
    private static final String TOP_UP_FORMAT_DESCRIPTION = "Top up €%.2f";
    private static final String BOOKING_PAYMENT_FORMAT_DESCRIPTION = "Booking Payment €%.2f";
    private static final String REFUND_FORMAT_DESCRIPTION = "Refund €%.2f";
    ;
    private static final String BOOKING_EARNING_FORMAT_DESCRIPTION = "Booking Earning €%.2f";
    private static final String EARNING_REVERSAL = "Earning Reversal €%.2f";

    private final WalletRepository walletRepository;

    private final TransactionService transactionService;
    private final PropertyService propertyService;

    @Autowired
    public WalletServiceImpl(WalletRepository walletRepository, TransactionService transactionService, PropertyService propertyService) {
        this.walletRepository = walletRepository;
        this.transactionService = transactionService;
        this.propertyService = propertyService;
    }

    @Override
    @Transactional
    public Transaction topUp(UUID walletId, BigDecimal amount, TransactionType transactionType) {
        Wallet wallet = this.walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletDoesNotExist("Wallet with such id [%s] does not exist".formatted(walletId)));

        wallet.setBalance(wallet.getBalance().add(amount));

        this.walletRepository.save(wallet);

        String description = (transactionType == TransactionType.DEPOSIT)
                ? TOP_UP_FORMAT_DESCRIPTION.formatted(amount)
                : BOOKING_EARNING_FORMAT_DESCRIPTION.formatted(amount);


        return this.transactionService.persistTransaction(
                wallet.getOwner(),
                STAY_CONNECTED,
                wallet.getId().toString(),
                amount,
                wallet.getBalance(),
                transactionType,
                TransactionStatus.SUCCEEDED,
                description,
                null
        );
    }

    @Override
    @LogCreation(entity = "wallet")
    public Wallet createWallet(User user) {
        Wallet wallet = Wallet.builder().balance(BigDecimal.valueOf(50)).owner(user).build();
        this.walletRepository.save(wallet);
        return wallet;
    }

    @Override
    public List<Transaction> getLastThreeTransactions(Wallet wallet) {
        return this.transactionService.getLastThreeTransactions(wallet);
    }

    @Override
    @Transactional
    public void exchange(CreateReservationRequest createReservationRequest, UUID ownerId) {

        Wallet reserverWallet = this.walletRepository.findByOwner_Id(createReservationRequest.getUserId());
        reserverWallet.setBalance(reserverWallet.getBalance().subtract(createReservationRequest.getTotalPrice()));

        this.walletRepository.save(reserverWallet);


        Wallet propertyOwnerWallet = this.walletRepository.findByOwner_Id(ownerId);
        topUp(propertyOwnerWallet.getId(), createReservationRequest.getTotalPrice(), TransactionType.BOOKING_EARNING);


        this.transactionService.persistTransaction(
                reserverWallet.getOwner(),
                reserverWallet.getId().toString(),
                STAY_CONNECTED,
                createReservationRequest.getTotalPrice(),
                reserverWallet.getBalance(),
                TransactionType.BOOKING_PAYMENT,
                TransactionStatus.SUCCEEDED,
                BOOKING_PAYMENT_FORMAT_DESCRIPTION.formatted(createReservationRequest.getTotalPrice()),
                null
        );

    }

    @Override
    @Transactional
    public void refund(UUID userId, BigDecimal totalPrice) {

        Wallet wallet = this.walletRepository.findByOwner_Id(userId);

        wallet.setBalance(wallet.getBalance().add(totalPrice));

        this.walletRepository.save(wallet);


        this.transactionService.persistTransaction(
                wallet.getOwner(),
                STAY_CONNECTED,
                wallet.getId().toString(),
                totalPrice,
                wallet.getBalance(),
                TransactionType.REFUND,
                TransactionStatus.SUCCEEDED,
                REFUND_FORMAT_DESCRIPTION.formatted(totalPrice),
                null
        );

    }

    @Override
    @Transactional
    public void reverseEarning(BigDecimal totalPrice, UUID propertyId) {
        Property property = this.propertyService.getById(propertyId);

        User propertyOwner = property.getOwner();

        Wallet propertyOwnerWallet = this.walletRepository.findByOwner_Id(propertyOwner.getId());

        propertyOwnerWallet.setBalance(propertyOwnerWallet.getBalance().subtract(totalPrice));

        this.walletRepository.save(propertyOwnerWallet);

        this.transactionService.persistTransaction(
                  propertyOwnerWallet.getOwner(),
                  propertyOwnerWallet.getId().toString(),
                  STAY_CONNECTED,
                  totalPrice,
                  propertyOwnerWallet.getBalance(),
                  TransactionType.EARNING_REVERSAL,
                  TransactionStatus.SUCCEEDED,
                  EARNING_REVERSAL.formatted(totalPrice),
                 null
        );
    }


}

