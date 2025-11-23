package com.example.stayconnected.location.service.impl;

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
    public Location createLocation(LocationRequest locationRequest) {
        Location location = new Location(
                locationRequest.getCountry(),
                locationRequest.getCity(),
                locationRequest.getAddress()
        );

        this.locationRepository.save(location);

        log.info("Location successfully created with country [%s], city [%s], address [%s]"
                .formatted(location.getCountry(), location.getCity(), location.getAddress()));

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
}
