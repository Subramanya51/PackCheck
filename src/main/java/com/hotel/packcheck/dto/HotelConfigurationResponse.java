package com.hotel.packcheck.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HotelConfigurationResponse {

    private Long hotelId;

    private String hotelName;

    private String country;

    private String state;

    private String city;

    private String postalCode;
}