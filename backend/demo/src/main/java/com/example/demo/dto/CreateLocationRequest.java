package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateLocationRequest {

    @JsonProperty("location_name")
    private String locationName;

    private String address;
    private Integer capacity;

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
}

