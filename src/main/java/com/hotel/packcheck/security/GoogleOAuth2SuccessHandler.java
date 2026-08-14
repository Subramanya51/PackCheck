package com.hotel.packcheck.security;

import com.hotel.packcheck.dto.AdminLoginResponse;
import com.hotel.packcheck.entity.Admin;
import com.hotel.packcheck.repository.AdminRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendBaseUrl;

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

        // =========================================================
        // GET GOOGLE USER
        // =========================================================

        OAuth2User oauth2User =
                (OAuth2User) authentication.getPrincipal();

        String email =
                oauth2User.getAttribute("email");


        // =========================================================
        // FIND ADMIN
        // =========================================================

        Admin admin =
                adminRepository
                        .findByEmailWithHotel(email)
                        .orElse(null);

        if (admin == null) {

            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Google account is not registered as an Admin."
            );

            return;
        }


        // =========================================================
        // GENERATE JWT
        // =========================================================

        String token =
                jwtUtility.generateToken(
                        admin.getEmail()
                );


        // =========================================================
        // BUILD LOGIN RESPONSE
        // =========================================================

        AdminLoginResponse loginResponse =
                new AdminLoginResponse(
                        token,
                        admin.getName(),
                        admin.getHotel().getHotelName()
                );


        // =========================================================
        // HTML RESPONSE
        // =========================================================

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");


        // =========================================================
        // ESCAPE VALUES FOR JAVASCRIPT
        // =========================================================

        String safeToken =
                escapeJavaScript(loginResponse.getToken());

        String safeUserName =
                escapeJavaScript(loginResponse.getUserName());

        String safeHotelName =
                escapeJavaScript(loginResponse.getHotelName());

        String safeEmail =
                escapeJavaScript(email);


        // =========================================================
        // FRONTEND ORIGIN
        // =========================================================

        String safeFrontendOrigin =
                escapeJavaScript(frontendBaseUrl);


        // =========================================================
        // HTML / JAVASCRIPT BRIDGE
        // =========================================================

        String html =
                "<!DOCTYPE html>"
                        + "<html>"
                        + "<head>"
                        + "<meta charset=\"UTF-8\">"
                        + "<title>Authentication Successful</title>"
                        + "</head>"
                        + "<body>"
                        + "<script>"

                        + "const authData = {"
                        + "type: 'GOOGLE_AUTH_SUCCESS',"
                        + "token: '" + safeToken + "',"
                        + "userName: '" + safeUserName + "',"
                        + "hotelName: '" + safeHotelName + "',"
                        + "email: '" + safeEmail + "'"
                        + "};"

                        + "if (window.opener) {"

                        + "window.opener.postMessage("
                        + "authData,"
                        + "'" + safeFrontendOrigin + "'"
                        + ");"

                        + "window.close();"

                        + "} else {"

                        + "window.location.href = '"
                        + safeFrontendOrigin
                        + "/oauth/google';"

                        + "}"

                        + "</script>"
                        + "</body>"
                        + "</html>";


        // =========================================================
        // SEND RESPONSE
        // =========================================================

        response.getWriter().write(html);
        response.getWriter().flush();
    }


    // =============================================================
    // JAVASCRIPT STRING ESCAPING
    // =============================================================

    private String escapeJavaScript(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("<", "\\u003C")
                .replace(">", "\\u003E")
                .replace("&", "\\u0026");
    }
}
//package com.hotel.packcheck.security;
//
//import com.hotel.packcheck.entity.Admin;
//import com.hotel.packcheck.dto.AdminLoginResponse;
//import com.hotel.packcheck.repository.AdminRepository;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
//@Component
//public class GoogleOAuth2SuccessHandler
//        implements AuthenticationSuccessHandler {
//
//    private final AdminRepository adminRepository;
//    private final JwtUtility jwtUtility;
//
//    public GoogleOAuth2SuccessHandler(
//            AdminRepository adminRepository,
//            JwtUtility jwtUtility) {
//
//        this.adminRepository = adminRepository;
//        this.jwtUtility = jwtUtility;
//    }
//
//    @Override
//    public void onAuthenticationSuccess(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            Authentication authentication)
//            throws IOException, ServletException {
//
//        OAuth2User oauth2User =
//                (OAuth2User) authentication.getPrincipal();
//
//        String email =
//                oauth2User.getAttribute("email");
//
//        Admin admin =
//                adminRepository.findByEmailWithHotel(email)
//                        .orElse(null);
//
//        if (admin == null) {
//            response.sendError(
//                    HttpServletResponse.SC_UNAUTHORIZED,
//                    "Google account is not registered as an Admin."
//            );
//            return;
//        }
//
//        String token =
//                jwtUtility.generateToken(admin.getEmail());
//
//        AdminLoginResponse loginResponse =
//                new AdminLoginResponse(
//                        token,
//                        admin.getName(),
//                        admin.getHotel().getHotelName()
//                );
//
//        response.setContentType("application/json");
//        response.setCharacterEncoding("UTF-8");
//
//        response.getWriter().write(
//                String.format(
//                        "{\"token\":\"%s\",\"userName\":\"%s\",\"hotelName\":\"%s\"}",
//                        loginResponse.getToken(),
//                        loginResponse.getUserName(),
//                        loginResponse.getHotelName()
//                )
//        );
//    }
//}