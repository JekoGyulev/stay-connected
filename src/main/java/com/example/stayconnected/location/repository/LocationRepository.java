package com.example.stayconnected.location.repository;

import com.example.stayconnected.web.dto.location.CityStatsDTO;
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

    @Query(value = "SELECT new com.example.stayconnected.dto.CityStatsDTO(l.city, COUNT(l)) " +
            "FROM Location l " +
            "GROUP BY l.city " +
            "ORDER BY COUNT(l.city) DESC " +
            "LIMIT 4")
    List<CityStatsDTO> findTop4PopularDestinations();
}
