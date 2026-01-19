package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateLocationRequest {

    @JsonProperty("location_name")
    private String locationName;

    private String address;
    private Integer capacity;
    private String city;

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

}

