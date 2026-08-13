package com.hotel.packcheck.dto;

import com.hotel.packcheck.enums.CartMode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartResponse {

    private Long cartRecordId;

    private String cartId;

    private CartMode mode;
}