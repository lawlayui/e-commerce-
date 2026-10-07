package com.lawlayui.e_commerce.account.application.port.in.dto;

import java.time.LocalDateTime;

public class UserProfileDto {
    private String profileId; 
    private String fullName; 
    private String phoneNumber; 
    private String address; 
    private String city; 
    private String postalCode; 
    private LocalDateTime updatedAt;

    public UserProfileDto(String profileId, String fullName, String phoneNumber,
        String address, String city, String postalCode, LocalDateTime updatedAt
    ) {
        this.profileId = profileId; 
        this.fullName = fullName; 
        this.phoneNumber = phoneNumber; 
        this.address = address; 
        this.city = city; 
        this.postalCode = postalCode; 
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
