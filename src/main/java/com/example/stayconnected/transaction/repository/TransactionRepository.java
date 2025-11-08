package com.example.stayconnected.transaction.repository;

import com.example.stayconnected.transaction.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findAllBySenderOrReceiverOrderByCreatedOnDesc(String sender, String receiver);
}
