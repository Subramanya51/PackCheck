package com.hotel.packcheck.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BellboyHeadRegistrationResponse {

    private Long bellboyHeadId;
    private String loginId;
    private String password;
}