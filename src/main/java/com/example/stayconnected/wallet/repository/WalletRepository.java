package com.example.stayconnected.wallet.repository;

import com.example.stayconnected.user.model.User;
import com.example.stayconnected.wallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    Optional<Wallet> findByOwner(User owner);
}
