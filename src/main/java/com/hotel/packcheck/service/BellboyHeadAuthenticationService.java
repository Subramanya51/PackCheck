package com.hotel.packcheck.service;

import com.hotel.packcheck.dto.BellboyHeadLoginRequest;
import com.hotel.packcheck.dto.BellboyHeadLoginResponse;
import com.hotel.packcheck.entity.BellboyHead;
import com.hotel.packcheck.repository.BellboyHeadRepository;
import com.hotel.packcheck.security.JwtUtility;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class BellboyHeadAuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final BellboyHeadRepository bellboyHeadRepository;
    private final JwtUtility jwtUtility;

    public BellboyHeadAuthenticationService(
            AuthenticationManager authenticationManager,
            BellboyHeadRepository bellboyHeadRepository,
            JwtUtility jwtUtility) {

        this.authenticationManager = authenticationManager;
        this.bellboyHeadRepository = bellboyHeadRepository;
        this.jwtUtility = jwtUtility;
    }

    public BellboyHeadLoginResponse login(
            BellboyHeadLoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getLoginId(),
                                request.getPassword()
                        )
                );

        BellboyHead bellboyHead =
                bellboyHeadRepository
                        .findByLoginId(authentication.getName())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Bellboy Head not found."
                                )
                        );

        String token =
                jwtUtility.generateToken(
                        authentication.getName()
                );

        return new BellboyHeadLoginResponse(
                token,
                "Bellboy Head",
                bellboyHead.getHotel().getHotelName()
        );
    }
}
