package com.hotel.packcheck.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BellboyHeadLoginResponse {

    private String token;
    private String userName;
    private String hotelName;
}