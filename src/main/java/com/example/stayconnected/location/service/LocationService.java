package com.example.stayconnected.location.service;

import com.example.stayconnected.location.model.Location;
import com.example.stayconnected.web.dto.location.LocationRequest;

import java.util.List;

public interface LocationService {

    Location createLocation(LocationRequest locationRequest);

    List<String> getAllDistinctCountries();
}
