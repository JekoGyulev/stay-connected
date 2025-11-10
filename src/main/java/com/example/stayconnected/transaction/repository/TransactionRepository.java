package com.example.stayconnected.transaction.repository;

import com.example.stayconnected.transaction.enums.TransactionStatus;
import com.example.stayconnected.transaction.enums.TransactionType;
import com.example.stayconnected.transaction.model.Transaction;
import jakarta.validation.Valid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.status = :status AND t.type IN :types AND t.createdOn BETWEEN :start AND :end")
    BigDecimal sumAmountByStatusAndTypeInAndCreatedOnBetween(
            @Param("status") TransactionStatus status,
            @Param("types") List<TransactionType> types,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );


    @Query("SELECT COALESCE(AVG(t.amount), 0) FROM Transaction t WHERE t.status = :status" +
            " AND t.type IN :types" +
            " AND t.createdOn BETWEEN :start AND :end ")
    BigDecimal getAverageTransactionAmountByStatusAndTypeInAndCreatedOnBetween(@Param("status") TransactionStatus status,
                                                  @Param(value = "types") List<TransactionType> types,
                                                  @Param(value = "start")LocalDateTime createdAfter,
                                                  @Param(value = "end")LocalDateTime createdBefore);

}
