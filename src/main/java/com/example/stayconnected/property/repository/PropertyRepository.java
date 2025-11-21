package com.example.stayconnected.property.repository;

import com.example.stayconnected.property.enums.CategoryType;
import com.example.stayconnected.property.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PropertyRepository extends JpaRepository<Property, UUID> {

    List<Property> findAllByOwnerId(UUID ownerId);

    List<Property> findAllByOrderByCreateDateDesc();

    List<Property> findByCategoryTypeAndLocation_CountryOrderByCreateDateDesc(
            CategoryType categoryType,
            String country
    );

    long countAllByCreateDateBetween(LocalDateTime createDateAfter, LocalDateTime createDateBefore);

    List<Property> findAllByCategoryTypeOrderByCreateDateDesc(CategoryType categoryType);

    List<Property> findAllByLocation_CountryOrderByCreateDateDesc(String country);
}
