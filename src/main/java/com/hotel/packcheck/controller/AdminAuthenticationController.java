package com.hotel.packcheck.controller;

import com.hotel.packcheck.dto.AdminLoginRequest;
import com.hotel.packcheck.dto.AdminLoginResponse;
import com.hotel.packcheck.service.AdminAuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminAuthenticationController {

    private final AdminAuthenticationService authenticationService;

    public AdminAuthenticationController(
            AdminAuthenticationService authenticationService) {

        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<AdminLoginResponse> login(
            @RequestBody AdminLoginRequest request) {

        AdminLoginResponse response =
                authenticationService.login(request);

        return ResponseEntity.ok(response);
    }
}