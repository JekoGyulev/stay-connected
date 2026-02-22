package com.example.stayconnected.property.repository;

import com.example.stayconnected.property.enums.CategoryType;
import com.example.stayconnected.property.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PropertyRepository extends JpaRepository<Property, UUID> {

    List<Property> findAllByOwnerIdOrderByCreateDateDescAverageRatingDesc(UUID ownerId);

    List<Property> findAllByOrderByCreateDateDescAverageRatingDesc();

    List<Property> findByCategoryTypeAndLocation_CountryOrderByCreateDateDescAverageRatingDesc(
            CategoryType categoryType,
            String country
    );

    long countAllByCreateDateBetween(LocalDateTime createDateAfter, LocalDateTime createDateBefore);

    List<Property> findAllByCategoryTypeOrderByCreateDateDescAverageRatingDesc(CategoryType categoryType);

    List<Property> findAllByLocation_CountryOrderByCreateDateDescAverageRatingDesc(String country);

    List<Property> findTop4ByOrderByAverageRatingDesc();

    @Query("""
        SELECT p FROM Property p WHERE p.id NOT IN (:propertyIdsNotAvailable) AND p.location.country = :country
    """)
    List<Property> findAllAvailableProperties(@Param(value = "propertyIdsNotAvailable") List<UUID> propertyIdsNotAvailable, @Param(value = "country") String country);
}
