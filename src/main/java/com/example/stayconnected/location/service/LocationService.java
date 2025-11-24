package com.example.stayconnected.location.service;

import com.example.stayconnected.dto.CityStatsDTO;
import com.example.stayconnected.location.model.Location;
import com.example.stayconnected.web.dto.location.LocationRequest;

import java.util.List;
import java.util.Map;

public interface LocationService {

    Location createLocation(LocationRequest locationRequest);

    List<String> getAllDistinctCountries();

    void updateLocation(Location location);

    List<CityStatsDTO> get4MostPopularDestinations();
}
