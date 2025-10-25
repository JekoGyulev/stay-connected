package com.example.stayconnected.user.repository;

import com.example.stayconnected.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    long countAllByActiveFalse();

    long countAllByActiveTrue();
}
