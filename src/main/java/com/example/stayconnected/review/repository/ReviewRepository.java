package com.example.stayconnected.review.repository;

import com.example.stayconnected.review.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    List<Review> findAllByPropertyIdOrderByCreatedAtDesc(UUID propertyId);

    @Query("""
        SELECT r.property.id, COALESCE( AVG(r.rating), 0)
            FROM Review r
                WHERE r.property.id IN :propertyIds
                    GROUP BY r.property.id
    """)
    List<Object[]> findAverageRatingsForProperties(@Param(value = "propertyIds") List<UUID> propertyIds);

    List<Review> findTop5ByPropertyIdOrderByCreatedAtDesc(@Param(value = "propertyId") UUID propertyId);

    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.property.id = :propertyId")
    BigDecimal findAverageRatingForProperty(@Param(value = "propertyId") UUID propertyId);
}
