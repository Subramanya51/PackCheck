package com.hotel.packcheck.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtility jwtUtility;
    private final AdminUserDetailsService adminUserDetailsService;
    private final BellboyHeadUserDetailsService bellboyHeadUserDetailsService;

    public JwtAuthFilter(
            JwtUtility jwtUtility,
            AdminUserDetailsService adminUserDetailsService,
            BellboyHeadUserDetailsService bellboyHeadUserDetailsService) {

        this.jwtUtility = jwtUtility;
        this.adminUserDetailsService = adminUserDetailsService;
        this.bellboyHeadUserDetailsService =
                bellboyHeadUserDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader =
                request.getHeader("Authorization");

        if (authorizationHeader == null ||
                !authorizationHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        String token =
                authorizationHeader.substring(7);

        if (!jwtUtility.isTokenValid(token)) {

            filterChain.doFilter(request, response);
            return;
        }

        String username =
                jwtUtility.extractUsername(token);

        UserDetails userDetails = null;

        try {
            userDetails =
                    adminUserDetailsService
                            .loadUserByUsername(username);
        } catch (Exception ignored) {
            // Not an Admin, try Bellboy Head.
        }

        if (userDetails == null) {
            try {
                userDetails =
                        bellboyHeadUserDetailsService
                                .loadUserByUsername(username);
            } catch (Exception ignored) {
                // User not found in either system.
            }
        }

        if (userDetails != null) {

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}