package com.hotel.packcheck.security;

import com.hotel.packcheck.entity.Admin;
import com.hotel.packcheck.dto.AdminLoginResponse;
import com.hotel.packcheck.repository.AdminRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GoogleOAuth2SuccessHandler
        implements AuthenticationSuccessHandler {

    private final AdminRepository adminRepository;
    private final JwtUtility jwtUtility;

    public GoogleOAuth2SuccessHandler(
            AdminRepository adminRepository,
            JwtUtility jwtUtility) {

        this.adminRepository = adminRepository;
        this.jwtUtility = jwtUtility;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {

        OAuth2User oauth2User =
                (OAuth2User) authentication.getPrincipal();

        String email =
                oauth2User.getAttribute("email");

        Admin admin =
                adminRepository.findByEmailWithHotel(email)
                        .orElse(null);

        if (admin == null) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Google account is not registered as an Admin."
            );
            return;
        }

        String token =
                jwtUtility.generateToken(admin.getEmail());

        AdminLoginResponse loginResponse =
                new AdminLoginResponse(
                        token,
                        admin.getName(),
                        admin.getHotel().getHotelName()
                );

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(
                String.format(
                        "{\"token\":\"%s\",\"userName\":\"%s\",\"hotelName\":\"%s\"}",
                        loginResponse.getToken(),
                        loginResponse.getUserName(),
                        loginResponse.getHotelName()
                )
        );
    }
}