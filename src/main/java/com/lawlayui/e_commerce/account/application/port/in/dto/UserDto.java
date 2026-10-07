package com.lawlayui.e_commerce.account.application.port.in.dto;

import java.time.LocalDateTime;
import java.util.List;

public class UserDto {
    private String userId; 
    private String email; 
    private String role; 
    private boolean isActive; 
    private List<UserProfileDto> userProfiles;
    private LocalDateTime createdAt; 
    private LocalDateTime updatedAt;

    public UserDto(String userId, String email, String role, boolean isActive, LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    public void setUserProfiles(List<UserProfileDto> userProfiles) {
        this.userProfiles = userProfiles;
    }
    public void addUserProfile(UserProfileDto userProfileDto) {
        this.userProfiles.add(userProfileDto);
    }
    public List<UserProfileDto> getUserProfiles() {
        return userProfiles;
    }
}
