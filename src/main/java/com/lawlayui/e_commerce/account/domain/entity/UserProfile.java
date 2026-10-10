package com.lawlayui.e_commerce.account.domain.entity;

import java.time.LocalDateTime;

import com.lawlayui.e_commerce.account.domain.value_object.Address;
import com.lawlayui.e_commerce.account.domain.value_object.PhoneNumber;

public class UserProfile {
    private String profileId;
    private String userId;
    private String fullName;
    private PhoneNumber phoneNumber;
    private Address address; 
    private LocalDateTime updatedAt;
    
    public void rename(String newFullName) {
        this.fullName = newFullName;
    }

    public void updateContactNumber(String countryCode, String newPhoneNumber) {
        this.phoneNumber = PhoneNumber.of(countryCode, newPhoneNumber);
    }

    public void relocateTo(String newAddress) {
        this.address = Address.of(newAddress);
    }

    public static UserProfile reconstitue(String profileId, String userId, String fullName,
        String phoneNumber, String countryCode, String address, 
        LocalDateTime updatedAt
    ) {
        return new UserProfile(profileId, userId, fullName, 
            PhoneNumber.of(countryCode, phoneNumber), Address.of(address), 
            updatedAt);
    }

    public static UserProfile create(String userId, String fullName, String phoneNumber, String countryCode,
        String address 
    ) {
        return new UserProfile(null, userId, fullName, PhoneNumber.of(countryCode, phoneNumber), Address.of(address), LocalDateTime.now());
    }
    
    private UserProfile(String profileId, String userId, String fullName, PhoneNumber phoneNumber, Address address,
            LocalDateTime updatedAt) {
        this.profileId = profileId;
        this.userId = userId;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.updatedAt = updatedAt;
    }

    public String getProfileId() {return profileId;}
    public String getUserId() {return userId;}
    public String getFullName() {return fullName;}
    public PhoneNumber getPhoneNumber() {return phoneNumber;}
    public Address getAddress() {return address;}
    public LocalDateTime getUpdatedAt() {return updatedAt;}
}
