package com.hotel.packcheck.service;

import com.hotel.packcheck.dto.AdminLoginRequest;
import com.hotel.packcheck.dto.AdminLoginResponse;
import com.hotel.packcheck.entity.Admin;
import com.hotel.packcheck.repository.AdminRepository;
import com.hotel.packcheck.security.JwtUtility;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AdminAuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final AdminRepository adminRepository;
    private final JwtUtility jwtUtility;

    public AdminAuthenticationService(
            AuthenticationManager authenticationManager,
            AdminRepository adminRepository,
            JwtUtility jwtUtility) {

        this.authenticationManager = authenticationManager;
        this.adminRepository = adminRepository;
        this.jwtUtility = jwtUtility;
    }

    public AdminLoginResponse login(AdminLoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        Admin admin = adminRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Admin not found.")
                );

        String token = jwtUtility.generateToken(
                admin.getEmail()
        );

        return new AdminLoginResponse(
                token,
                admin.getName(),
                admin.getHotel().getHotelName()
        );
    }
}