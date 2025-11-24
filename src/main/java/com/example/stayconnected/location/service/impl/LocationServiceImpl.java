package com.example.stayconnected.location.service.impl;

import com.example.stayconnected.dto.CityStatsDTO;
import com.example.stayconnected.location.model.Location;
import com.example.stayconnected.location.repository.LocationRepository;
import com.example.stayconnected.location.service.LocationService;
import com.example.stayconnected.web.dto.location.LocationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Override
    public List<CityStatsDTO> get4MostPopularDestinations() {
        return this.locationRepository
                .findTop4PopularDestinations();
    }
}
