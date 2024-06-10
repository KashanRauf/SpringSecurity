package com.kashan.security.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kashan.security.config.JwtService;
import com.kashan.security.user.Role;
import com.kashan.security.user.User;
import com.kashan.security.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    // Tto interact with the database
    private final UserRepository repository;
    // To encode the password before storing
    private final PasswordEncoder passwordEncoder;
    // To generate a token
    private final JwtService jwtService;

    // To handle authentication
    private final AuthenticationManager authenticationManager;

    // Create a new user and save them to the database, generate a token
    public AuthenticationResponse register(RegisterRequest request) {
        // Create a new user
        User user = User.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword())) // Must encode the password
            .role(Role.USER)
            .build();
        // Save to the database
        repository.save(user);

        // Generate a token with no extra claims
        String jwtToken = jwtService.generateToken(user);

        // Return a response containing the token
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // Throws an exception if the credentials are incorrect
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(), 
                request.getPassword()
            )
        );

        // If the authentication succeeds, return the user
        User user = repository.findByEmail(request.getEmail())
            .orElseThrow();

        // Generate a token with no extra claims
        String jwtToken = jwtService.generateToken(user);

        // Return a response containing the token
        return AuthenticationResponse.builder().token(jwtToken).build();
    }
}
