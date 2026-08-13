package com.hotel.packcheck.dto;

import com.hotel.packcheck.enums.CartMode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartModeRequest {

    private String cartId;
    private CartMode mode;
}