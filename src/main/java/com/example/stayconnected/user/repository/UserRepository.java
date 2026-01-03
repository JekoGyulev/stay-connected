package com.example.stayconnected.user.repository;

import com.example.stayconnected.user.enums.UserRole;
import com.example.stayconnected.user.model.User;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Page<User> findAllByOrderByRegisteredAtDescUsernameAsc(Pageable pageable);

    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = :active")
    long countAllByActiveIs(@Param(value = "active") boolean active);

    long countAllByRegisteredAtBetween(LocalDateTime registeredAtAfter, LocalDateTime registeredAtBefore);

    Page<User> findAllByRoleAndIsActiveOrderByRegisteredAtDescUsernameAsc(UserRole role, boolean isActive, Pageable pageable);

    Page<User> findAllByRoleOrderByRegisteredAtDescUsernameAsc(UserRole role, Pageable pageable);

    Page<User> findAllByIsActiveOrderByRegisteredAtDescUsernameAsc(boolean isActive, Pageable pageable);

    Optional<User> findByEmail(String email);

    @Query(
        """
            SELECT u FROM User u
            WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
            ORDER BY u.registeredAt DESC
        """
    )
    Page<User> findAllByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrderByRegisteredAtDesc(String search, Pageable pageable);
}
