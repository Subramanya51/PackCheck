package com.hotel.packcheck.controller;

import com.hotel.packcheck.dto.BellboyHeadLoginRequest;
import com.hotel.packcheck.dto.BellboyHeadLoginResponse;
import com.hotel.packcheck.dto.BellboyHeadRegistrationRequest;
import com.hotel.packcheck.dto.BellboyHeadRegistrationResponse;
import com.hotel.packcheck.service.BellboyHeadAuthenticationService;
import com.hotel.packcheck.service.BellboyHeadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.hotel.packcheck.dto.BellboyHeadDeleteRequest;
import com.hotel.packcheck.entity.Admin;
import com.hotel.packcheck.security.AdminUserDetails;
import org.springframework.security.core.Authentication;
@RestController
@RequestMapping("/bellboy-head")
public class BellboyHeadController {

    private final BellboyHeadAuthenticationService
            bellboyHeadAuthenticationService;

    private final BellboyHeadService bellboyHeadService;

    public BellboyHeadController(
            BellboyHeadAuthenticationService bellboyHeadAuthenticationService,
            BellboyHeadService bellboyHeadService) {

        this.bellboyHeadAuthenticationService =
                bellboyHeadAuthenticationService;

        this.bellboyHeadService =
                bellboyHeadService;
    }

    @PostMapping("/register")
    public ResponseEntity<BellboyHeadRegistrationResponse> register(
            @RequestBody BellboyHeadRegistrationRequest request) {

        BellboyHeadRegistrationResponse response =
                bellboyHeadService.register(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<BellboyHeadLoginResponse> login(
            @RequestBody BellboyHeadLoginRequest request) {

        BellboyHeadLoginResponse response =
                bellboyHeadAuthenticationService.login(request);

        return ResponseEntity.ok(response);
    }
    @DeleteMapping
    public ResponseEntity<Void> delete(
            Authentication authentication,
            @RequestBody BellboyHeadDeleteRequest request) {

        AdminUserDetails adminUserDetails =
                (AdminUserDetails) authentication.getPrincipal();

        Admin admin =
                adminUserDetails.getAdmin();

        Long hotelId =
                admin.getHotel().getHotelId();

        bellboyHeadService.deleteBellboyHead(
                request.getLoginId(),
                hotelId
        );

        return ResponseEntity.noContent().build();
    }
}