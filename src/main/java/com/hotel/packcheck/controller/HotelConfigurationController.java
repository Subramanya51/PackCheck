package com.hotel.packcheck.controller;

import com.hotel.packcheck.dto.HotelConfigurationRequest;
import com.hotel.packcheck.dto.HotelConfigurationResponse;
import com.hotel.packcheck.dto.HotelDetailsUpdateRequest;
import com.hotel.packcheck.security.AdminUserDetails;
import com.hotel.packcheck.service.HotelConfigurationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/packcheck/configuration")
public class HotelConfigurationController {

    private final HotelConfigurationService hotelConfigurationService;

    public HotelConfigurationController(
            HotelConfigurationService hotelConfigurationService) {

        this.hotelConfigurationService = hotelConfigurationService;
    }

    @PostMapping
    public ResponseEntity<HotelConfigurationResponse> configureHotel(
            @RequestBody HotelConfigurationRequest request) {

        HotelConfigurationResponse response =
                hotelConfigurationService.configureHotel(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    @PatchMapping
    public ResponseEntity<HotelConfigurationResponse> updateHotelDetails(
            Authentication authentication,
            @RequestBody HotelDetailsUpdateRequest request) {

        AdminUserDetails adminUserDetails =
                (AdminUserDetails) authentication.getPrincipal();

        Long hotelId =
                adminUserDetails
                        .getAdmin()
                        .getHotel()
                        .getHotelId();

        HotelConfigurationResponse response =
                hotelConfigurationService.updateHotelDetails(
                        hotelId,
                        request
                );

        return ResponseEntity.ok(response);
    }
}