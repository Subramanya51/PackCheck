package com.hotel.packcheck.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class HotelConfigurationRequest {

    private String adminName;

    private String adminEmail;

    private String adminPassword;

    private String hotelName;

    private String country;

    private String state;

    private String city;

    private String postalCode;

    private int numberOfFloors;
}