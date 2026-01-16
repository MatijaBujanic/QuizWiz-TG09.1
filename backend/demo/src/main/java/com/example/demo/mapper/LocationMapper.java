package com.example.demo.mapper;

import com.example.demo.dto.CreateLocationRequest;
import com.example.demo.dto.LocationResponse;
import com.example.demo.model.Location;
import com.example.demo.model.LocationRow;

public class LocationMapper {

    public static LocationResponse toResponse(Location l) {
        LocationResponse r = new LocationResponse();
        r.setLocationId(l.getLocationId());
        r.setLocationName(l.getLocationName());
        r.setAddress(l.getAddress());
        r.setCapacity(l.getCapacity());
        return r;
    }
    public static LocationRow toRow(CreateLocationRequest r) {
        LocationRow row = new LocationRow();
        row.setLocationName(r.getLocationName());
        row.setAddress(r.getAddress());
        row.setCapacity(r.getCapacity());
        return row;
    }

}
