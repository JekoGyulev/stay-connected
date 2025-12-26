package com.example.stayconnected.location.service.impl;

import com.example.stayconnected.aop.annotations.LogCreation;
import com.example.stayconnected.web.dto.location.CityStatsDTO;
import com.example.stayconnected.location.model.Location;
import com.example.stayconnected.location.repository.LocationRepository;
import com.example.stayconnected.location.service.LocationService;
import com.example.stayconnected.web.dto.location.LocationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    @Autowired
    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }


    @Override
    @LogCreation(entity = "location")
    public Location createLocation(LocationRequest locationRequest) {
        Location location = Location.builder().city(locationRequest.getCity())
                .country(locationRequest.getCountry())
                .address(locationRequest.getAddress()).build();

        this.locationRepository.save(location);

        return location;
    }

    @Override
    public List<String> getAllDistinctCountries() {
        return this.locationRepository.findDistinctCountries();
    }

    @Override
    public void updateLocation(Location location) {
        this.locationRepository.save(location);
    }

    @Override
    public List<CityStatsDTO> get4MostPopularDestinations() {
        return this.locationRepository
                .findTop4PopularDestinations();
    }
}
