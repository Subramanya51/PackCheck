package com.hotel.packcheck.security;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


@Configuration
public class SecurityConfig {

    private final UserDetailsService adminUserDetailsService;
    private final UserDetailsService bellboyHeadUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthFilter jwtAuthFilter;
    private final GoogleOAuth2SuccessHandler googleOAuth2SuccessHandler;


    public SecurityConfig(
            @Qualifier("adminUserDetailsService")
            UserDetailsService adminUserDetailsService,

            @Qualifier("bellboyHeadUserDetailsService")
            UserDetailsService bellboyHeadUserDetailsService,

            PasswordEncoder passwordEncoder,

            JwtAuthFilter jwtAuthFilter,

            GoogleOAuth2SuccessHandler googleOAuth2SuccessHandler) {

        this.adminUserDetailsService =
                adminUserDetailsService;

        this.bellboyHeadUserDetailsService =
                bellboyHeadUserDetailsService;

        this.passwordEncoder =
                passwordEncoder;

        this.jwtAuthFilter =
                jwtAuthFilter;

        this.googleOAuth2SuccessHandler =
                googleOAuth2SuccessHandler;
    }


    // =========================================================
    // CORS CONFIGURATION
    // =========================================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:3000"
                )
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of(
                        "Authorization",
                        "Content-Type",
                        "Accept"
                )
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }


    // =========================================================
    // ADMIN AUTHENTICATION PROVIDER
    // =========================================================

    @Bean
    public AuthenticationProvider adminAuthenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(
                        adminUserDetailsService
                );

        provider.setPasswordEncoder(
                passwordEncoder
        );

        return provider;
    }


    // =========================================================
    // BELLBOY HEAD AUTHENTICATION PROVIDER
    // =========================================================

    @Bean
    public AuthenticationProvider bellboyHeadAuthenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(
                        bellboyHeadUserDetailsService
                );

        provider.setPasswordEncoder(
                passwordEncoder
        );

        return provider;
    }


    // =========================================================
    // AUTHENTICATION MANAGER
    // =========================================================

    @Bean
    public AuthenticationManager authenticationManager() {

        return new ProviderManager(
                adminAuthenticationProvider(),
                bellboyHeadAuthenticationProvider()
        );
    }


    // =========================================================
    // SECURITY FILTER CHAIN
    // =========================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

                // -------------------------------------------------
                // CSRF
                // -------------------------------------------------

                .csrf(csrf ->
                        csrf.disable()
                )


                // -------------------------------------------------
                // CORS
                // -------------------------------------------------

                .cors(cors ->
                        cors.configurationSource(
                                corsConfigurationSource()
                        )
                )


                // -------------------------------------------------
                // SESSION MANAGEMENT
                // -------------------------------------------------

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.IF_REQUIRED
                        )
                )


                // -------------------------------------------------
                // AUTHORIZATION
                // -------------------------------------------------

                .authorizeHttpRequests(auth -> auth


                        // -------------------------------------------------
                        // CORS PREFLIGHT
                        // -------------------------------------------------

                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        ).permitAll()


                        // -------------------------------------------------
                        // PUBLIC AUTHENTICATION ENDPOINTS
                        // -------------------------------------------------

                        .requestMatchers(
                                "/admin/login",
                                "/health",
                                "/packcheck/configuration",
                                "/bellboy-head/register",
                                "/bellboy-head/login",
                                "/oauth2/**",
                                "/login/**"
                        ).permitAll()


                        // -------------------------------------------------
                        // ADMIN-ONLY CART REGISTRATION
                        // -------------------------------------------------

                        .requestMatchers(
                                HttpMethod.POST,
                                "/packcheck/carts"
                        ).hasRole("ADMIN")


                        // -------------------------------------------------
                        // ADMIN-ONLY CART MODE CHANGE
                        // -------------------------------------------------

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/packcheck/carts/mode"
                        ).hasRole("ADMIN")


                        // -------------------------------------------------
                        // ADMIN-ONLY BELLBOY HEAD DELETION
                        // -------------------------------------------------

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/bellboy-head"
                        ).hasRole("ADMIN")


                        // -------------------------------------------------
                        // ADMIN-ONLY CART DELETION
                        // -------------------------------------------------

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/packcheck/carts"
                        ).hasRole("ADMIN")


                        // -------------------------------------------------
                        // ADMIN-ONLY FLOOR CREATION
                        // -------------------------------------------------

                        .requestMatchers(
                                HttpMethod.POST,
                                "/packcheck/floors"
                        ).hasRole("ADMIN")


                        // -------------------------------------------------
                        // ADMIN-ONLY FLOOR DELETION
                        // -------------------------------------------------

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/packcheck/floors"
                        ).hasRole("ADMIN")


                        // -------------------------------------------------
                        // CART COUNT
                        // Admin + Bellboy Head
                        // -------------------------------------------------

                        .requestMatchers(
                                HttpMethod.GET,
                                "/packcheck/carts/active/count"
                        ).authenticated()


                        // -------------------------------------------------
                        // EVERYTHING ELSE
                        // -------------------------------------------------

                        .anyRequest().authenticated()
                )


                // =========================================================
                // EXCEPTION HANDLING
                // =========================================================

                .exceptionHandling(exception ->
                        exception
                                .defaultAuthenticationEntryPointFor(
                                        (request,
                                         response,
                                         authException) -> {

                                            response.setStatus(
                                                    HttpServletResponse
                                                            .SC_UNAUTHORIZED
                                            );
                                        },

                                        request ->
                                                request
                                                        .getRequestURI()
                                                        .startsWith(
                                                                "/packcheck/"
                                                        )
                                )
                )


                // =========================================================
                // AUTHENTICATION PROVIDERS
                // =========================================================

                .authenticationProvider(
                        adminAuthenticationProvider()
                )

                .authenticationProvider(
                        bellboyHeadAuthenticationProvider()
                )


                // =========================================================
                // GOOGLE OAUTH2
                // =========================================================

                .oauth2Login(oauth2 ->
                        oauth2
                                .successHandler(
                                        googleOAuth2SuccessHandler
                                )
                )


                // =========================================================
                // JWT FILTER
                // =========================================================

                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                );


        return http.build();
    }
}
//package com.hotel.packcheck.security;
//
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.authentication.ProviderManager;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.HttpStatusEntryPoint;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//
//import java.util.List;
//
//@Configuration
//public class SecurityConfig {
//
//    private final UserDetailsService adminUserDetailsService;
//    private final UserDetailsService bellboyHeadUserDetailsService;
//    private final PasswordEncoder passwordEncoder;
//    private final JwtAuthFilter jwtAuthFilter;
//    private final GoogleOAuth2SuccessHandler googleOAuth2SuccessHandler;
//    public SecurityConfig(
//            @Qualifier("adminUserDetailsService")
//            UserDetailsService adminUserDetailsService,
//
//            @Qualifier("bellboyHeadUserDetailsService")
//            UserDetailsService bellboyHeadUserDetailsService,
//
//            PasswordEncoder passwordEncoder,
//            JwtAuthFilter jwtAuthFilter,
//            GoogleOAuth2SuccessHandler googleOAuth2SuccessHandler) {
//
//        this.adminUserDetailsService = adminUserDetailsService;
//        this.bellboyHeadUserDetailsService =
//                bellboyHeadUserDetailsService;
//        this.passwordEncoder = passwordEncoder;
//        this.jwtAuthFilter = jwtAuthFilter;
//        this.googleOAuth2SuccessHandler =
//                googleOAuth2SuccessHandler;
//    }
//
//    @Bean
//    public AuthenticationProvider adminAuthenticationProvider() {
//
//        DaoAuthenticationProvider provider =
//                new DaoAuthenticationProvider(
//                        adminUserDetailsService
//                );
//
//        provider.setPasswordEncoder(passwordEncoder);
//
//        return provider;
//    }
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//
//        CorsConfiguration configuration = new CorsConfiguration();
//
//        configuration.setAllowedOrigins(
//                List.of("http://localhost:3000")
//        );
//
//        configuration.setAllowedMethods(
//                List.of(
//                        "GET",
//                        "POST",
//                        "PUT",
//                        "PATCH",
//                        "DELETE",
//                        "OPTIONS"
//                )
//        );
//
//        configuration.setAllowedHeaders(
//                List.of(
//                        "Authorization",
//                        "Content-Type",
//                        "Accept"
//                )
//        );
//
//        configuration.setAllowCredentials(true);
//
//        UrlBasedCorsConfigurationSource source =
//                new UrlBasedCorsConfigurationSource();
//
//        source.registerCorsConfiguration(
//                "/**",
//                configuration
//        );
//
//        return source;
//    }
//
//    @Bean
//    public AuthenticationProvider bellboyHeadAuthenticationProvider() {
//
//        DaoAuthenticationProvider provider =
//                new DaoAuthenticationProvider(
//                        bellboyHeadUserDetailsService
//                );
//
//        provider.setPasswordEncoder(passwordEncoder);
//
//        return provider;
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager() {
//
//        return new ProviderManager(
//                adminAuthenticationProvider(),
//                bellboyHeadAuthenticationProvider()
//        );
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(
//            HttpSecurity http) throws Exception {
//
//        http
//                .csrf(csrf -> csrf.disable()).cors(cors -> {})
//
//                .sessionManagement(session ->
//                        session.sessionCreationPolicy(
//                                SessionCreationPolicy.IF_REQUIRED
//                        )
//                )
//
//                .authorizeHttpRequests(auth -> auth
//
//                        // Public authentication endpoints
//                        .requestMatchers(
//                                "/admin/login","/health","/packcheck/configuration",
//                                "/bellboy-head/register",
//                                "/bellboy-head/login",
//                                "/oauth2/**",
//                                "/login/**"
//                        ).permitAll()
//
//                        // Admin-only cart operations
//                        .requestMatchers(
//                                org.springframework.http.HttpMethod.POST,
//                                "/packcheck/carts"
//                        ).hasRole("ADMIN")
//                        .requestMatchers(
//                                HttpMethod.PATCH,
//                                "/packcheck/carts/mode"
//                        ).hasRole("ADMIN")
//                        .requestMatchers(
//                                HttpMethod.DELETE,
//                                "/bellboy-head"
//                        ).hasRole("ADMIN")
//
//                        .requestMatchers(
//                                org.springframework.http.HttpMethod.DELETE,
//                                "/packcheck/carts"
//                        ).hasRole("ADMIN")
//                        // Admin-only floor operations
//                        .requestMatchers(
//                                HttpMethod.POST,
//                                "/packcheck/floors"
//                        ).hasRole("ADMIN")
//
//                        .requestMatchers(
//                                HttpMethod.DELETE,
//                                "/packcheck/floors"
//                        ).hasRole("ADMIN")
//
//                        // Active cart count for authenticated users
//                        .requestMatchers(
//                                org.springframework.http.HttpMethod.GET,
//                                "/packcheck/carts/active/count"
//                        ).authenticated()
//
//                        .anyRequest().authenticated()
//                ).exceptionHandling(exception -> exception
//                        .defaultAuthenticationEntryPointFor(
//                                (request, response, authException) -> {
//                                    response.setStatus(
//                                            HttpServletResponse.SC_UNAUTHORIZED
//                                    );
//                                },
//                                request ->
//                                        request.getRequestURI()
//                                                .startsWith("/packcheck/")
//                        )
//                )
//
//                .authenticationProvider(
//                        adminAuthenticationProvider()
//                )
//
//                .authenticationProvider(
//                        bellboyHeadAuthenticationProvider()
//                )
//
//                .oauth2Login(oauth2 ->
//                        oauth2
//                                .successHandler(
//                                        googleOAuth2SuccessHandler
//                                )
//                )
//
//                .addFilterBefore(
//                        jwtAuthFilter,
//                        UsernamePasswordAuthenticationFilter.class
//                );
//
//        return http.build();
//    }
//}