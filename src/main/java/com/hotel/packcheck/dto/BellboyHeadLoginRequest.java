package com.hotel.packcheck.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BellboyHeadLoginRequest {

    private String loginId;

    private String password;
}