package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Location {

    @JsonProperty("location_id")
    private Integer locationId;

    @JsonProperty("location_name")
    private String locationName;

    private String address;

    private Integer capacity;

    public Location() {}

    public Location(Integer locationId, String locationName,
                    String address, Integer capacity) {
        this.locationId = locationId;
        this.locationName = locationName;
        this.address = address;
        this.capacity = capacity;
    }

    public Integer getLocationId() { return locationId; }
    public void setLocationId(Integer locationId) { this.locationId = locationId; }

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
}
