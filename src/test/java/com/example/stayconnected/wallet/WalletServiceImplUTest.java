package com.example.stayconnected.wallet;


import com.example.stayconnected.property.model.Property;
import com.example.stayconnected.property.service.PropertyService;
import com.example.stayconnected.reservation.client.dto.CreateReservationRequest;
import com.example.stayconnected.transaction.enums.TransactionStatus;
import com.example.stayconnected.transaction.enums.TransactionType;
import com.example.stayconnected.transaction.model.Transaction;
import com.example.stayconnected.transaction.service.TransactionService;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.wallet.model.Wallet;
import com.example.stayconnected.wallet.repository.WalletRepository;
import com.example.stayconnected.wallet.service.WalletService;
import com.example.stayconnected.wallet.service.impl.WalletServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WalletServiceImplUTest {

    @Mock
    private  WalletRepository walletRepository;
    @Mock
    private  TransactionService transactionService;
    @Mock
    private  PropertyService propertyService;
    @InjectMocks
    private WalletServiceImpl walletServiceImpl;



    @Test
    void whenCreateWallet_thenSaveToDB() {

        User user = User.builder().id(UUID.randomUUID()).build();

        Wallet wallet = Wallet.builder().id(user.getId()).owner(user).build();

        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        Wallet result = walletServiceImpl.createWallet(user);

        assertEquals(user.getId(), result.getOwner().getId());
        verify(walletRepository).save(any(Wallet.class));
    }

    @Test
    void shouldRefundMoney_whenWalletExists() {
        UUID userId = UUID.randomUUID();

        User user = User.builder().id(userId).build();

        Wallet wallet = Wallet.builder()
                .id(userId)
                .owner(user)
                .balance(BigDecimal.valueOf(50))
                .build();

        BigDecimal totalPrice = BigDecimal.valueOf(50);

        when(walletRepository.findByOwner_Id(userId)).thenReturn(wallet);

        walletServiceImpl.refund(userId, totalPrice);

        assertEquals(BigDecimal.valueOf(100), wallet.getBalance());

        verify(walletRepository).save(wallet);

        verify(transactionService).persistTransaction(
                eq(user),
                eq("STAY_CONNECTED"), // adjust if constant is different
                eq(wallet.getId().toString()),
                eq(totalPrice),
                eq(wallet.getBalance()),
                eq(TransactionType.REFUND),
                eq(TransactionStatus.SUCCEEDED),
                anyString(),
                isNull()
        );
    }


    @Test
    void shouldReverseEarning_whenPropertyExists() {
        UUID propertyId = UUID.randomUUID();
        BigDecimal totalPrice = BigDecimal.valueOf(50);

        User propertyOwner = User.builder()
                .id(UUID.randomUUID())
                .build();

        Property property = Property.builder()
                .id(propertyId)
                .owner(propertyOwner)
                .build();


        Wallet wallet = Wallet.builder()
                .id(UUID.randomUUID())
                .owner(propertyOwner)
                .balance(BigDecimal.valueOf(100))
                .build();


        when(propertyService.getById(propertyId)).thenReturn(property);
        when(walletRepository.findByOwner_Id(propertyOwner.getId())).thenReturn(wallet);


        walletServiceImpl.reverseEarning(totalPrice, propertyId);


        assertEquals(BigDecimal.valueOf(50), wallet.getBalance());


        verify(walletRepository).save(wallet);

        verify(transactionService).persistTransaction(
                eq(propertyOwner),
                eq(wallet.getId().toString()),
                eq("STAY_CONNECTED"), // adjust constant if needed
                eq(totalPrice),
                eq(wallet.getBalance()),
                eq(TransactionType.EARNING_REVERSAL),
                eq(TransactionStatus.SUCCEEDED),
                anyString(),
                isNull()
        );
    }



}
