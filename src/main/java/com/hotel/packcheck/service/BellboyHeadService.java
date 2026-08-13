package com.hotel.packcheck.service;

import com.hotel.packcheck.dto.BellboyHeadRegistrationRequest;
import com.hotel.packcheck.dto.BellboyHeadRegistrationResponse;
import com.hotel.packcheck.entity.BellboyHead;
import com.hotel.packcheck.entity.Hotel;
import com.hotel.packcheck.repository.BellboyHeadRepository;
import com.hotel.packcheck.repository.HotelRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
public class BellboyHeadService {

    private final BellboyHeadRepository bellboyHeadRepository;
    private final HotelRepository hotelRepository;
    private final PasswordEncoder passwordEncoder;

    private final SecureRandom secureRandom = new SecureRandom();

    public BellboyHeadService(
            BellboyHeadRepository bellboyHeadRepository,
            HotelRepository hotelRepository,
            PasswordEncoder passwordEncoder) {

        this.bellboyHeadRepository = bellboyHeadRepository;
        this.hotelRepository = hotelRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public BellboyHeadRegistrationResponse register(
            BellboyHeadRegistrationRequest request) {

        Hotel hotel = hotelRepository
                .findByHotelName(request.getHotelName())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Hotel not found."
                        ));

        /*
         * Only one operational Bellboy Head account
         * should exist for a hotel.
         */
        if (bellboyHeadRepository
                .findByHotelHotelId(hotel.getHotelId())
                .isPresent()) {

            throw new IllegalArgumentException(
                    "Bellboy Head account already exists for this hotel."
            );
        }

        String loginId = generateLoginId();
        String rawPassword = generatePassword();

        BellboyHead bellboyHead = new BellboyHead();

        bellboyHead.setLoginId(loginId);
        bellboyHead.setPassword(
                passwordEncoder.encode(rawPassword)
        );
        bellboyHead.setHotel(hotel);

        BellboyHead saved =
                bellboyHeadRepository.save(bellboyHead);

        return new BellboyHeadRegistrationResponse(
                saved.getBellboyHeadId(),
                loginId,
                rawPassword
        );
    }

    private String generateLoginId() {

        String loginId;

        do {
            loginId = "BBH-" + randomAlphaNumeric(6);
        } while (
                bellboyHeadRepository
                        .findByLoginId(loginId)
                        .isPresent()
        );

        return loginId;
    }

    private String generatePassword() {

        return randomAlphaNumeric(10)
                + "@"
                + (secureRandom.nextInt(90) + 10);
    }

    private String randomAlphaNumeric(int length) {

        final String characters =
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                        + "abcdefghijklmnopqrstuvwxyz"
                        + "0123456789";

        StringBuilder result =
                new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            result.append(
                    characters.charAt(
                            secureRandom.nextInt(
                                    characters.length()
                            )
                    )
            );
        }

        return result.toString();
    }
}