package com.example.stayconnected.property.repository;

import com.example.stayconnected.property.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PropertyRepository extends JpaRepository<Property, UUID> {

    List<Property> findAllByOwnerId(UUID ownerId);
}
