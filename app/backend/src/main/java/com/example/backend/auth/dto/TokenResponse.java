package com.example.backend.auth.dto;

public class TokenResponse {

    private String tokenType;
    private String accessToken;
    private String refreshToken;
    private long expiresInSeconds;

    public TokenResponse(String tokenType, String accessToken, String refreshToken, long expiresInSeconds) {
        this.tokenType = tokenType;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresInSeconds = expiresInSeconds;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public long getExpiresInSeconds() {
        return expiresInSeconds;
    }
}
