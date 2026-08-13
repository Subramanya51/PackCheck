package com.hotel.packcheck.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartCountResponse {

    private long activeCarts;
    private long maintenanceCarts;
}