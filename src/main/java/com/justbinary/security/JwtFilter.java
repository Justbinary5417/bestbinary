package com.justbinary.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication
    .UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context
    .SecurityContextHolder;
import org.springframework.security.core.userdetails
    .UserDetails;
import org.springframework.security.core.userdetails
    .UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter
    .OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtFilter(
            JwtUtil jwtUtil,
            UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain)
            throws ServletException, IOException {

        try {
            String header =
                request.getHeader("Authorization");

            if (header != null
                    && header.startsWith("Bearer ")) {

                String token = header.substring(7);
                String email =
                    jwtUtil.extractEmail(token);

                if (email != null
                    && SecurityContextHolder
                        .getContext()
                        .getAuthentication() == null) {

                    UserDetails userDetails =
                        userDetailsService
                            .loadUserByUsername(email);

                    if (jwtUtil.isTokenValid(
                            token, email)) {

                        UsernamePasswordAuthenticationToken
                            auth =
                            new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails
                                    .getAuthorities());

                        SecurityContextHolder
                            .getContext()
                            .setAuthentication(auth);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(
                "JWT Error: " + e.getMessage());
        }

        chain.doFilter(request, response);
    }
}