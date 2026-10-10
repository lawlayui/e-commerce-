package com.lawlayui.e_commerce.account.application.port.in.dto;

import java.time.LocalDateTime;

public class UserProfileDto {
    private String profileId; 
    private String fullName; 
    private String phoneNumber; 
    private String address; 

    private LocalDateTime updatedAt;

    public UserProfileDto(String profileId, String fullName, String phoneNumber,
        String address, LocalDateTime updatedAt
    ) {
        this.profileId = profileId; 
        this.fullName = fullName; 
        this.phoneNumber = phoneNumber; 
        this.address = address; 
        this.updatedAt = updatedAt;
    }

    
    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
