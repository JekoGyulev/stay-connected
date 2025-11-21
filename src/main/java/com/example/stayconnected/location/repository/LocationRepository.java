package com.example.stayconnected.location.repository;

import com.example.stayconnected.location.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LocationRepository extends JpaRepository<Location, UUID> {

    @Query("SELECT DISTINCT l.country FROM Location l")
    List<String> findDistinctCountries();
}
