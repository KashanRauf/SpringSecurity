package com.kashan.security.config;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Service
public class JwtService {
    private static String SECRET_KEY;
    
    // Secret key is provided by a configuration file that is not included in the repo
    @Value(value = "${jwt.secret}")
    private String secretKey;

    @PostConstruct
    private void init() {
        SECRET_KEY = secretKey;
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolveClaims) {
        final Claims allClaims = extractAllClaims(token);
        return resolveClaims.apply(allClaims);
    }

    private Key getSigningKey() {
        byte[] key = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(key);
    }

    private String generateToken(Map<String, Object> otherClaims, UserDetails user) {
        // getUsername() will return the email, just named that way
        // Token expires after 24 hours (86400000 ms)

        long issueTime = System.currentTimeMillis();
        long expiryTime = issueTime + 86400000;

        return Jwts.builder().setClaims(otherClaims).setSubject(user.getUsername())
            .setIssuedAt(new Date(issueTime)).setExpiration(new Date(expiryTime))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
    }

    public String generateToken(UserDetails user) {
        return generateToken(new HashMap<>(), user);
    }

    public boolean validateToken(String token, UserDetails user) {
        final String email = extractEmail(token);
        return (email.equals(user.getUsername())) && !tokenExpired(token);
    }

    private boolean tokenExpired(String token) {
        return getExpiration(token).before(new Date(System.currentTimeMillis()));
    }

    private Date getExpiration(String token) {
        return (Date) extractClaim(token, Claims::getExpiration);
    }
}
