package com.kashan.security.config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
// Need to activate on every request
public class JwtAuthFilter extends OncePerRequestFilter {

    // Needed to make use of the token
    private final JwtService jwts;
    // For checking if the user exists
    private final UserDetailsService uds;

    @SuppressWarnings("null") // IDK what null is but remove 'unused' annotation when function is fully implemented    
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        // Need to extract JWT authentication token
        final String authHeader = request.getHeader("Authorization");
        final String token;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // 7 because token comes after "Bearing "
        token = authHeader.substring(7);
        userEmail = jwts.extractEmail(token);

        // If the user hasn't been authenticated yet, must first perform the authentication by updating the SecurityContextHolder
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Gets the user if they exist
            UserDetails userData = this.uds.loadUserByUsername(userEmail);

            if (jwts.validateToken(token, userData)) {
                // Update the SCH and send request to dispatcher servlet
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userData, userData.getAuthorities());
                authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
            filterChain.doFilter(request, response);
        }
    }
}
