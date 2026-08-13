package com.hotel.packcheck.controller;

import com.hotel.packcheck.dto.FloorRequest;
import com.hotel.packcheck.dto.FloorResponse;
import com.hotel.packcheck.entity.Admin;
import com.hotel.packcheck.entity.Hotel;
import com.hotel.packcheck.security.AdminUserDetails;
import com.hotel.packcheck.service.FloorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/packcheck/floors")
public class FloorController {

    private final FloorService floorService;

    public FloorController(FloorService floorService) {
        this.floorService = floorService;
    }

    @PostMapping
    public ResponseEntity<FloorResponse> addFloor(
            Authentication authentication,
            @RequestBody FloorRequest request) {

        AdminUserDetails adminUserDetails =
                (AdminUserDetails) authentication.getPrincipal();

        Admin admin =
                adminUserDetails.getAdmin();

        Hotel hotel =
                admin.getHotel();

        FloorResponse response =
                floorService.addFloor(
                        hotel,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteFloor(
            Authentication authentication,
            @RequestBody FloorRequest request) {

        AdminUserDetails adminUserDetails =
                (AdminUserDetails) authentication.getPrincipal();

        Admin admin =
                adminUserDetails.getAdmin();

        Hotel hotel =
                admin.getHotel();

        floorService.deleteFloor(
                hotel,
                request.getFloorNumber()
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}