package com.hotel.packcheck.controller;

import com.hotel.packcheck.dto.CartRegistrationRequest;
import com.hotel.packcheck.dto.CartResponse;
import com.hotel.packcheck.entity.Admin;
import com.hotel.packcheck.entity.BellboyHead;
import com.hotel.packcheck.entity.Hotel;
import com.hotel.packcheck.security.AdminUserDetails;
import com.hotel.packcheck.security.BellboyHeadUserDetails;
import com.hotel.packcheck.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/packcheck/carts")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping
    public ResponseEntity<CartResponse> addCart(
            Authentication authentication,
            @RequestBody CartRegistrationRequest request) {

        AdminUserDetails adminUserDetails =
                (AdminUserDetails) authentication.getPrincipal();

        Admin admin = adminUserDetails.getAdmin();

        Hotel hotel = admin.getHotel();

        CartResponse response = cartService.addCart(
                request.getCartId(),
                hotel
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/active/count")
    public ResponseEntity<Long> getActiveCartCount(
            Authentication authentication) {

        Long hotelId;

        if (authentication.getPrincipal()
                instanceof AdminUserDetails adminUserDetails) {

            hotelId =
                    adminUserDetails
                            .getAdmin()
                            .getHotel()
                            .getHotelId();

        } else if (authentication.getPrincipal()
                instanceof BellboyHeadUserDetails bellboyHeadUserDetails) {

            hotelId =
                    bellboyHeadUserDetails
                            .getBellboyHead()
                            .getHotel()
                            .getHotelId();

        } else {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }

        long count =
                cartService.getActiveCartCount(hotelId);

        return ResponseEntity.ok(count);
    }

    @DeleteMapping
    public ResponseEntity<Void> removeCart(
            Authentication authentication,
            @RequestBody CartRegistrationRequest request) {

        AdminUserDetails adminUserDetails =
                (AdminUserDetails) authentication.getPrincipal();

        Admin admin = adminUserDetails.getAdmin();

        Long hotelId =
                admin.getHotel().getHotelId();

        cartService.removeCart(
                request.getCartId(),
                hotelId
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}