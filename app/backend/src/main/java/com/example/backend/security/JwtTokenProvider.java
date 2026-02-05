package com.example.backend.security;

import com.example.backend.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private final JwtProperties properties;

    public JwtTokenProvider(JwtProperties properties) {
        this.properties = properties;
    }

    public String createAccessToken(Long userId, String email, Collection<? extends GrantedAuthority> authorities) {
        Instant now = Instant.now();
        Instant expiry = now.plus(properties.getAccessTokenMinutes(), ChronoUnit.MINUTES);
        String roles = authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

        return Jwts.builder()
            .setIssuer(properties.getIssuer())
            .setSubject(String.valueOf(userId))
            .setId(UUID.randomUUID().toString())
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(expiry))
            .claim("email", email)
            .claim("auth", roles)
            .claim("typ", "access")
            .signWith(Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
            .compact();
    }

    public String createRefreshToken(Long userId, String email) {
        Instant now = Instant.now();
        Instant expiry = now.plus(properties.getRefreshTokenDays(), ChronoUnit.DAYS);

        return Jwts.builder()
            .setIssuer(properties.getIssuer())
            .setSubject(String.valueOf(userId))
            .setId(UUID.randomUUID().toString())
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(expiry))
            .claim("email", email)
            .claim("typ", "refresh")
            .signWith(Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
            .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        String subject = claims.getSubject();
        String roles = claims.get("auth", String.class);
        List<SimpleGrantedAuthority> authorities = List.of();
        if (roles != null && !roles.isBlank()) {
            authorities = List.of(roles.split(",")).stream()
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        }
        return new UsernamePasswordAuthenticationToken(subject, token, authorities);
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
            .requireIssuer(properties.getIssuer())
            .setSigningKey(Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8)))
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
}
