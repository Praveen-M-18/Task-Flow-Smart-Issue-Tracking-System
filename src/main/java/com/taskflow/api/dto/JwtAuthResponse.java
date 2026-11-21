package com.taskflow.api.dto;

public class JwtAuthResponse {

    private String token;
    private String accessToken;
    private String tokenType = "Bearer";
    private UserResponse user;

    public JwtAuthResponse() {
    }

    public JwtAuthResponse(String token, UserResponse user) {
        this.token = token;
        this.accessToken = token;
        this.user = user;
    }

    public String getToken() {
        return token != null ? token : accessToken;
    }

    public void setToken(String token) {
        this.token = token;
        this.accessToken = token;
    }

    public String getAccessToken() {
        return accessToken != null ? accessToken : token;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        this.token = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }
}
