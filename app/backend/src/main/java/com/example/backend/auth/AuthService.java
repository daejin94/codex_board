package com.example.backend.auth;

import com.example.backend.auth.dto.LoginRequest;
import com.example.backend.auth.dto.RefreshRequest;
import com.example.backend.auth.dto.SignupRequest;
import com.example.backend.auth.dto.TokenResponse;
import com.example.backend.config.JwtProperties;
import com.example.backend.security.JwtTokenProvider;
import com.example.backend.user.User;
import com.example.backend.user.UserGrant;
import com.example.backend.user.UserGrantRepository;
import com.example.backend.user.UserRepository;
import io.jsonwebtoken.Claims;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final String REFRESH_PREFIX = "refresh:";

    private final UserRepository userRepository;
    private final UserGrantRepository grantRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final JwtProperties jwtProperties;
    private final StringRedisTemplate redisTemplate;

    public AuthService(UserRepository userRepository,
                       UserGrantRepository grantRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider,
                       JwtProperties jwtProperties,
                       StringRedisTemplate redisTemplate) {
        this.userRepository = userRepository;
        this.grantRepository = grantRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.jwtProperties = jwtProperties;
        this.redisTemplate = redisTemplate;
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPw())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        List<GrantedAuthority> authorities = resolveAuthorities(user);

        String accessToken = tokenProvider.createAccessToken(user.getId(), user.getEmail(), authorities);
        String refreshToken = tokenProvider.createRefreshToken(user.getId(), user.getEmail());

        storeRefreshToken(refreshToken, user.getId());

        return new TokenResponse("Bearer", accessToken, refreshToken, jwtProperties.getAccessTokenMinutes() * 60);
    }

    @Transactional(readOnly = true)
    public TokenResponse refresh(RefreshRequest request) {
        Claims claims = tokenProvider.parseClaims(request.getRefreshToken());
        String type = claims.get("typ", String.class);
        if (!"refresh".equals(type)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String jti = claims.getId();
        String key = REFRESH_PREFIX + jti;
        String userIdValue = redisTemplate.opsForValue().get(key);
        if (userIdValue == null) {
            throw new IllegalArgumentException("Refresh token expired or revoked");
        }

        Long userId = Long.valueOf(userIdValue);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<GrantedAuthority> authorities = resolveAuthorities(user);

        redisTemplate.delete(key);

        String accessToken = tokenProvider.createAccessToken(user.getId(), user.getEmail(), authorities);
        String refreshToken = tokenProvider.createRefreshToken(user.getId(), user.getEmail());
        storeRefreshToken(refreshToken, user.getId());

        return new TokenResponse("Bearer", accessToken, refreshToken, jwtProperties.getAccessTokenMinutes() * 60);
    }

    @Transactional
    public void signup(SignupRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        UserGrant grant = new UserGrant("ROLE_USER");
        grant = grantRepository.save(grant);

        Instant now = Instant.now();
        User user = new User(
            request.getEmail(),
            passwordEncoder.encode(request.getPassword()),
            request.getPhone(),
            now,
            now,
            null,
            false,
            grant
        );
        userRepository.save(user);
    }

    public void logout(RefreshRequest request) {
        Claims claims = tokenProvider.parseClaims(request.getRefreshToken());
        String jti = claims.getId();
        redisTemplate.delete(REFRESH_PREFIX + jti);
    }

    private void storeRefreshToken(String refreshToken, Long userId) {
        Claims claims = tokenProvider.parseClaims(refreshToken);
        String key = REFRESH_PREFIX + claims.getId();
        Duration ttl = Duration.ofDays(jwtProperties.getRefreshTokenDays());
        redisTemplate.opsForValue().set(key, String.valueOf(userId), ttl);
    }

    private List<GrantedAuthority> resolveAuthorities(User user) {
        if (user.getRole() == null || user.getRole().getRoleName() == null) {
            return List.of();
        }
        return List.of(new SimpleGrantedAuthority(user.getRole().getRoleName()));
    }
}