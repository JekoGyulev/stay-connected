package com.example.stayconnected.location.service;

import com.example.stayconnected.location.model.Location;
import com.example.stayconnected.web.dto.location.LocationRequest;

public interface LocationService {

    Location createLocation(LocationRequest locationRequest);
}
