package com.hotel.packcheck.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


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

        this.adminUserDetailsService = adminUserDetailsService;
        this.bellboyHeadUserDetailsService =
                bellboyHeadUserDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtAuthFilter = jwtAuthFilter;
        this.googleOAuth2SuccessHandler =
                googleOAuth2SuccessHandler;
    }

    @Bean
    public AuthenticationProvider adminAuthenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(
                        adminUserDetailsService
                );

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
    public AuthenticationProvider bellboyHeadAuthenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(
                        bellboyHeadUserDetailsService
                );

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {

        return new ProviderManager(
                adminAuthenticationProvider(),
                bellboyHeadAuthenticationProvider()
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.IF_REQUIRED
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // Public authentication endpoints
                        .requestMatchers(
                                "/admin/login","/health",
                                "/bellboy-head/register",
                                "/bellboy-head/login",
                                "/oauth2/**",
                                "/login/**"
                        ).permitAll()

                        // Admin-only cart operations
                        .requestMatchers(
                                org.springframework.http.HttpMethod.POST,
                                "/packcheck/carts"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                org.springframework.http.HttpMethod.DELETE,
                                "/packcheck/carts"
                        ).hasRole("ADMIN")
                        // Admin-only floor operations
                        .requestMatchers(
                                HttpMethod.POST,
                                "/packcheck/floors"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/packcheck/floors"
                        ).hasRole("ADMIN")

                        // Active cart count for authenticated users
                        .requestMatchers(
                                org.springframework.http.HttpMethod.GET,
                                "/packcheck/carts/active/count"
                        ).authenticated()

                        .anyRequest().authenticated()
                ).exceptionHandling(exception -> exception
                        .defaultAuthenticationEntryPointFor(
                                (request, response, authException) -> {
                                    response.setStatus(
                                            HttpServletResponse.SC_UNAUTHORIZED
                                    );
                                },
                                request ->
                                        request.getRequestURI()
                                                .startsWith("/packcheck/")
                        )
                )

                .authenticationProvider(
                        adminAuthenticationProvider()
                )

                .authenticationProvider(
                        bellboyHeadAuthenticationProvider()
                )

                .oauth2Login(oauth2 ->
                        oauth2
                                .successHandler(
                                        googleOAuth2SuccessHandler
                                )
                )

                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}