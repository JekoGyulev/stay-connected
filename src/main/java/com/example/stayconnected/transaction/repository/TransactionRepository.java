package com.example.stayconnected.transaction.repository;

import com.example.stayconnected.transaction.enums.TransactionStatus;
import com.example.stayconnected.transaction.enums.TransactionType;
import com.example.stayconnected.transaction.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findAllBySenderOrReceiverOrderByCreatedOnDesc(String sender, String receiver);

    List<Transaction> findAllByOwner_IdOrderByCreatedOnDesc(UUID userId);

    List<Transaction> findAllByStatusAndTypeAndOwner_IdOrderByCreatedOnDesc
            (TransactionStatus status, TransactionType type, UUID userId);

    List<Transaction> findAllByTypeAndOwner_IdOrderByCreatedOnDesc(TransactionType transactionType, UUID userId);

    List<Transaction> findAllByStatusAndOwner_IdOrderByCreatedOnDesc(TransactionStatus transactionStatus, UUID userId);

    List<Transaction> findAllByStatus(TransactionStatus transactionStatus);

    long countAllByStatusAndTypeIn(TransactionStatus status, List<TransactionType> types);
}
