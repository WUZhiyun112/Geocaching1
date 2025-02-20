package com.example.backend.dto;

public class UserResponse {
    private boolean success;
    private String message;
    private String token;
    private String username;
    private String email;

    public UserResponse(boolean success, String message, String token, String username, String email) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.username = username;
        this.email = email;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
