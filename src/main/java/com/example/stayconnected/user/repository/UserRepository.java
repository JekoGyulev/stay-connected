package com.example.stayconnected.user.repository;

import com.example.stayconnected.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = :active")
    long countAllByActiveIs(@Param(value = "active") boolean active);

    long countAllByRegisteredAtBetween(LocalDateTime registeredAtAfter, LocalDateTime registeredAtBefore);
}
