package com.kashan.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    // At startup, Spring looks for a SecurityFilterChain bean
    // Responsible for configuring HTTP security
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        
        /* Deprecated example from video
        http.csrf((csrf) -> csrf.disable())
            .authorizeHttpRequests(a) // Determine how requests are authorized
            .requestMatchers("Patterns here").permitAll() // Whitelists the requests (e.g. endpoints that don't/won't have a token)
            .anyRequest().authenticated() // Any unspecified request requires authentication
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        */

        // CSRF is enabled by default but makes no difference for non-browser clients
        http.csrf((csrf) -> csrf.disable())
            // Specifies the rules for authorizing HTTP requests
            .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                // Whitelists provided requests from anyone
                // e.g. endpoints like signup/login when there isn't a token to use
                .requestMatchers("/api/v1/auth/**").permitAll()
                // Any unspecified request must have an authenticated user
                // Several other specifications such as denyAll and hasRole
                .requestMatchers("/api/v1/demo-controller").authenticated()
                .anyRequest().authenticated())
            .sessionManagement((sessionManagement) -> sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
            


        return http.build();
    }
}
