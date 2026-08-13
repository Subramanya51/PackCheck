package com.hotel.packcheck.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminLoginResponse {

    private String token;
    private String userName;
    private String hotelName;
}