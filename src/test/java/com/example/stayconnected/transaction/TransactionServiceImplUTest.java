package com.example.stayconnected.transaction;


import com.example.stayconnected.transaction.enums.TransactionStatus;
import com.example.stayconnected.transaction.enums.TransactionType;
import com.example.stayconnected.transaction.model.Transaction;
import com.example.stayconnected.transaction.repository.TransactionRepository;
import com.example.stayconnected.transaction.service.TransactionService;
import com.example.stayconnected.transaction.service.impl.TransactionServiceImpl;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.wallet.model.Wallet;
import com.example.stayconnected.web.dto.transaction.FilterTransactionRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplUTest {
    @Mock
    private TransactionRepository transactionRepository;
    @InjectMocks
    private TransactionServiceImpl transactionServiceImpl;


    @Test
    void whenTransactionIsGiven_persistTransaction() {

        com.example.stayconnected.user.model.User user = new User();


        Transaction transaction = Transaction.builder()
                .reasonForFailure(null)
                .balanceLeft(BigDecimal.valueOf(30))
                .amount(BigDecimal.valueOf(10))
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.SUCCEEDED)
                .owner(user)
                .createdOn(LocalDateTime.now())
                .receiver("Jeko")
                .description("description")
                .sender("Viktor").build();

        when(transactionRepository.save(any()))
                .thenReturn(transaction);

        Transaction result = transactionServiceImpl
                .persistTransaction(
                                    user,
                                    "Viktor",
                                    "Jeko",
                                    BigDecimal.valueOf(10),
                                    BigDecimal.valueOf(30),
                                    TransactionType.DEPOSIT,
                                    TransactionStatus.SUCCEEDED,
                                    "description",
                                    null
                );

        assertEquals(transaction.getAmount(), result.getAmount());
        assertEquals(transaction.getBalanceLeft(), result.getBalanceLeft());
        assertEquals(transaction.getReasonForFailure(), result.getReasonForFailure());
        assertEquals(transaction.getOwner(), result.getOwner());
        assertEquals(transaction.getCreatedOn(), result.getCreatedOn());

        verify(transactionRepository).save(any());
    }


    @Test
    void whenTransactionDoesNotExist_thenThrowException() {

        UUID transactionId = UUID.randomUUID();

        Transaction transaction = Transaction.builder()
                .id(transactionId)
                .build();

        when(transactionRepository.findById(any())).thenReturn(Optional.empty());


        assertThrows(RuntimeException.class, () -> transactionServiceImpl.getTransactionById(transactionId));
    }


    @Test
    void whenTransactionExists_thenReturnTransaction() {

        UUID transactionId = UUID.randomUUID();

        Transaction transaction = Transaction.builder()
                .id(transactionId)
                .amount(BigDecimal.valueOf(10))
                .build();

        when(transactionRepository.findById(any())).thenReturn(Optional.of(transaction));


        Transaction result = transactionServiceImpl.getTransactionById(transactionId);
        assertNotNull(result);
        assertEquals(transaction.getAmount(), result.getAmount());

        verify(transactionRepository).findById(any());
    }


    @Test
    void whenThereAreThreeTransactions_thenReturnTheLast3Transactions() {

        Transaction transaction1 = Transaction.builder()
                .id(UUID.randomUUID())
                .amount(BigDecimal.valueOf(10))
                .status(TransactionStatus.SUCCEEDED)
                .build();


        Transaction transaction2 = Transaction.builder()
                .id(UUID.randomUUID())
                .amount(BigDecimal.valueOf(10))
                .status(TransactionStatus.SUCCEEDED)
                .build();

        Transaction transaction3 = Transaction.builder()
                .id(UUID.randomUUID())
                .amount(BigDecimal.valueOf(10))
                .status(TransactionStatus.SUCCEEDED)
                .build();

        Wallet wallet = Wallet.builder()
                .id(UUID.randomUUID())
                .build();

        when(transactionRepository.findAllBySenderOrReceiverOrderByCreatedOnDesc(any(), any()))
                .thenReturn(List.of(transaction1, transaction2, transaction3));

        List<Transaction> result = transactionServiceImpl.getLastThreeTransactions(wallet);

        assertEquals(3, result.size());
        verify(transactionRepository).findAllBySenderOrReceiverOrderByCreatedOnDesc(any(), any());
    }


//    @Test
//    void whenUserIdProvided_thenReturnAllTransactionsForUser() {
//        UUID userId =  UUID.randomUUID();
//
//        User user = User.builder().id(userId).build();
//
//        Transaction transaction1 = Transaction.builder()
//                .id(UUID.randomUUID())
//                .amount(BigDecimal.valueOf(15))
//                .owner(user)
//                .status(TransactionStatus.SUCCEEDED)
//                .build();
//
//
//        Transaction transaction2 = Transaction.builder()
//                .id(UUID.randomUUID())
//                .owner(user)
//                .amount(BigDecimal.valueOf(10))
//                .status(TransactionStatus.SUCCEEDED)
//                .build();
//
//
//        when(transactionRepository.findAllByOwner_IdOrderByCreatedOnDesc(userId))
//                .thenReturn(List.of(transaction1, transaction2));
//
//        List<Transaction> transactions = transactionServiceImpl.getTransactionsByUserId(userId);
//
//        assertEquals(2, transactions.size());
//        verify(transactionRepository).findAllByOwner_IdOrderByCreatedOnDesc(userId);
//
//    }

    @Test
    void whenThereAreTransactions_returnThemAll() {

        Transaction transaction1 = Transaction.builder()
                .id(UUID.randomUUID())
                .amount(BigDecimal.valueOf(15))
                .status(TransactionStatus.SUCCEEDED)
                .build();

        when(transactionRepository.findAll())
                .thenReturn(List.of(transaction1));

        assertEquals(1,  transactionServiceImpl.getAllTransactions().size());
        verify(transactionRepository).findAll();
    }


    @Test
    void whenThereAreThreeTypesOfTransactions_andNeedToCalculateTotalRevenue_thenReturnTotalRevenue() {

        Transaction transaction1 = Transaction.builder()
                .id(UUID.randomUUID())
                .amount(BigDecimal.valueOf(15))
                .status(TransactionStatus.SUCCEEDED)
                .type(TransactionType.DEPOSIT)
                .build();

        Transaction transaction2 = Transaction.builder()
                .id(UUID.randomUUID())
                .amount(BigDecimal.valueOf(15))
                .status(TransactionStatus.SUCCEEDED)
                .type(TransactionType.BOOKING_PAYMENT)
                .build();

        Transaction transaction3 = Transaction.builder()
                .id(UUID.randomUUID())
                .amount(BigDecimal.valueOf(15))
                .status(TransactionStatus.SUCCEEDED)
                .type(TransactionType.REFUND)
                .build();


        when(transactionRepository.findAll())
                .thenReturn(List.of(transaction1, transaction2, transaction3));


        BigDecimal totalRevenue = transactionServiceImpl.getTotalRevenue();

        assertEquals(BigDecimal.valueOf(30), totalRevenue);
        verify(transactionRepository).findAll();
    }


    @Test
    void whenGetFailedTransaction_andThereAre2_and1OfThemIsFailed_thenReturnFailed() {

        Transaction transaction1 = Transaction.builder()
                .id(UUID.randomUUID())
                .amount(BigDecimal.valueOf(15))
                .status(TransactionStatus.SUCCEEDED)
                .type(TransactionType.BOOKING_PAYMENT)
                .build();

        Transaction transaction2 = Transaction.builder()
                .id(UUID.randomUUID())
                .amount(BigDecimal.valueOf(15))
                .status(TransactionStatus.FAILED)
                .type(TransactionType.DEPOSIT)
                .build();

        when(transactionRepository.findAllByStatus(TransactionStatus.FAILED))
                .thenReturn(List.of(transaction2));


        List<Transaction> allFailedTransactions = transactionServiceImpl.getAllFailedTransactions();

        assertEquals(1, allFailedTransactions.size());
        verify(transactionRepository).findAllByStatus(TransactionStatus.FAILED);
    }



//    @Test
//    void whenGetFilteredTransactions_andFiltersAreAll_thenReturnAllTransactions() {
//
//        User user = User.builder().id(UUID.randomUUID()).build();
//
//        Transaction transaction1 = Transaction.builder()
//                .id(UUID.randomUUID())
//                .amount(BigDecimal.valueOf(15))
//                .owner(user)
//                .status(TransactionStatus.SUCCEEDED)
//                .type(TransactionType.BOOKING_PAYMENT)
//                .build();
//
//        Transaction transaction2 = Transaction.builder()
//                .id(UUID.randomUUID())
//                .owner(user)
//                .amount(BigDecimal.valueOf(15))
//                .status(TransactionStatus.FAILED)
//                .type(TransactionType.DEPOSIT)
//                .build();
//
//
//        FilterTransactionRequest dto = FilterTransactionRequest.builder().transactionStatus("ALL").transactionType("ALL").build();
//
//
//        when(transactionRepository.findAllByOwner_IdOrderByCreatedOnDesc(user.getId()))
//                .thenReturn(List.of(transaction1, transaction2));
//
//        List<Transaction> result = transactionServiceImpl.getFilteredTransactions(user.getId(), dto);
//
//        assertEquals(2, result.size());
//        verify(transactionRepository).findAllByOwner_IdOrderByCreatedOnDesc(user.getId());
//
//    }


//    @Test
//    void whenGetFilteredTransactions_andFiltersAreSucceededAndTypeIsBookingPayment_thenReturn1Transaction() {
//
//        User user = User.builder().id(UUID.randomUUID()).build();
//
//        Transaction transaction1 = Transaction.builder()
//                .id(UUID.randomUUID())
//                .amount(BigDecimal.valueOf(15))
//                .owner(user)
//                .status(TransactionStatus.SUCCEEDED)
//                .type(TransactionType.BOOKING_PAYMENT)
//                .build();
//
//        Transaction transaction2 = Transaction.builder()
//                .id(UUID.randomUUID())
//                .owner(user)
//                .amount(BigDecimal.valueOf(15))
//                .status(TransactionStatus.FAILED)
//                .type(TransactionType.DEPOSIT)
//                .build();
//
//
//        FilterTransactionRequest dto = FilterTransactionRequest.builder()
//                .transactionStatus("SUCCEEDED")
//                .transactionType("BOOKING_PAYMENT").build();
//
//
//        when(transactionRepository
//                .findAllByStatusAndTypeAndOwner_IdOrderByCreatedOnDesc(TransactionStatus.SUCCEEDED, TransactionType.BOOKING_PAYMENT, user.getId()))
//                .thenReturn(List.of(transaction1));
//
//        List<Transaction> result = transactionServiceImpl.getFilteredTransactions(user.getId(), dto);
//
//        assertEquals(1, result.size());
//        verify(transactionRepository).findAllByStatusAndTypeAndOwner_IdOrderByCreatedOnDesc(TransactionStatus.SUCCEEDED, TransactionType.BOOKING_PAYMENT, user.getId());
//
//    }

//    @Test
//    void whenGetFilteredTransactions_andFilterIsTypeIsBookingPayment_thenReturn1Transaction() {
//
//        User user = User.builder().id(UUID.randomUUID()).build();
//
//        Transaction transaction1 = Transaction.builder()
//                .id(UUID.randomUUID())
//                .amount(BigDecimal.valueOf(15))
//                .owner(user)
//                .status(TransactionStatus.SUCCEEDED)
//                .type(TransactionType.BOOKING_PAYMENT)
//                .build();
//
//        Transaction transaction2 = Transaction.builder()
//                .id(UUID.randomUUID())
//                .owner(user)
//                .amount(BigDecimal.valueOf(15))
//                .status(TransactionStatus.FAILED)
//                .type(TransactionType.BOOKING_PAYMENT)
//                .build();
//
//
//        FilterTransactionRequest dto = FilterTransactionRequest.builder()
//                .transactionStatus("ALL")
//                .transactionType("BOOKING_PAYMENT").build();
//
//
//        when(transactionRepository
//                .findAllByTypeAndOwner_IdOrderByCreatedOnDesc(TransactionType.BOOKING_PAYMENT, user.getId()))
//                .thenReturn(List.of(transaction1, transaction2));
//
//        List<Transaction> result = transactionServiceImpl.getFilteredTransactions(user.getId(), dto);
//
//        assertEquals(2, result.size());
//        verify(transactionRepository).findAllByTypeAndOwner_IdOrderByCreatedOnDesc(TransactionType.BOOKING_PAYMENT, user.getId());
//
//    }

//    @Test
//    void whenGetFilteredTransactions_andFilterIsStatusIsSucceeded_thenReturn1Transaction() {
//
//        User user = User.builder().id(UUID.randomUUID()).build();
//
//        Transaction transaction1 = Transaction.builder()
//                .id(UUID.randomUUID())
//                .amount(BigDecimal.valueOf(15))
//                .owner(user)
//                .status(TransactionStatus.SUCCEEDED)
//                .type(TransactionType.BOOKING_PAYMENT)
//                .build();
//
//        Transaction transaction2 = Transaction.builder()
//                .id(UUID.randomUUID())
//                .owner(user)
//                .amount(BigDecimal.valueOf(15))
//                .status(TransactionStatus.FAILED)
//                .type(TransactionType.BOOKING_PAYMENT)
//                .build();
//
//
//        FilterTransactionRequest dto = FilterTransactionRequest.builder()
//                .transactionStatus("SUCCEEDED")
//                .transactionType("ALL").build();
//
//
//        when(transactionRepository
//                .findAllByStatusAndOwner_IdOrderByCreatedOnDesc(TransactionStatus.SUCCEEDED, user.getId()))
//                .thenReturn(List.of(transaction1));
//
//        List<Transaction> result = transactionServiceImpl.getFilteredTransactions(user.getId(), dto);
//
//        assertEquals(1, result.size());
//        verify(transactionRepository).findAllByStatusAndOwner_IdOrderByCreatedOnDesc(TransactionStatus.SUCCEEDED, user.getId());
//
//    }


    @Test
    void whenThereAreTwoTransactionsOfDifferentType_thenCalculateAverageAmount() {
        Transaction transaction1 = Transaction.builder()
                .id(UUID.randomUUID())
                .amount(BigDecimal.valueOf(15))
                .status(TransactionStatus.SUCCEEDED)
                .type(TransactionType.DEPOSIT)
                .build();

        Transaction transaction2 = Transaction.builder()
                .id(UUID.randomUUID())
                .amount(BigDecimal.valueOf(15))
                .status(TransactionStatus.SUCCEEDED)
                .type(TransactionType.BOOKING_PAYMENT)
                .build();


        when(transactionRepository.findAll()).thenReturn(List.of(transaction1, transaction2));

        when(transactionRepository.countAllByStatusAndTypeIn(TransactionStatus.SUCCEEDED,
                List.of(TransactionType.DEPOSIT, TransactionType.BOOKING_PAYMENT)))
                .thenReturn(2L);

        BigDecimal result = transactionServiceImpl.getAverageTransactionAmount();

        assertEquals(0, result.compareTo(BigDecimal.valueOf(15)));
        verify(transactionRepository).countAllByStatusAndTypeIn(TransactionStatus.SUCCEEDED, List.of(TransactionType.DEPOSIT, TransactionType.BOOKING_PAYMENT));
        verify(transactionRepository).findAll();
    }

    @Test
    void whenThereAreNoTransactions_thenCalculateAverageToBe0() {

        when(transactionRepository.findAll()).thenReturn(Collections.emptyList());

        when(transactionRepository.countAllByStatusAndTypeIn(TransactionStatus.SUCCEEDED,
                List.of(TransactionType.DEPOSIT, TransactionType.BOOKING_PAYMENT)))
                .thenReturn(0L);

        BigDecimal result = transactionServiceImpl.getAverageTransactionAmount();

        assertEquals(0, result.compareTo(BigDecimal.valueOf(0)));
        verify(transactionRepository).countAllByStatusAndTypeIn(TransactionStatus.SUCCEEDED, List.of(TransactionType.DEPOSIT, TransactionType.BOOKING_PAYMENT));
        verify(transactionRepository).findAll();
    }







}
