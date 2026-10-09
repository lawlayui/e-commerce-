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
    private String city; 
    private String postalCode;
    private LocalDateTime updatedAt;
    
    public void rename(String newFullName) {
        this.fullName = newFullName;
    }

    public void updateContactNumber(String countryCode, String newPhoneNumber) {
        this.phoneNumber = PhoneNumber.of(countryCode, newPhoneNumber);
    }

    public void relocateTo(String newAddress, String newCity, String newPostalCode) {
        this.address = Address.of(newAddress);
        this.city = newCity;
        this.postalCode = newPostalCode;
    }

    public static UserProfile intializeProfile(String profileId, String userId,
        String phoneNumber, String countryCode, String address, String city, String postalCode, 
        LocalDateTime updatedAt
    ) {
        return new UserProfile(profileId, userId, address, 
            PhoneNumber.of(countryCode, phoneNumber), Address.of(address), 
            city, postalCode, updatedAt);
    }
    
    private UserProfile(String profileId, String userId, String fullName, PhoneNumber phoneNumber, Address address,
            String city, String postalCode, LocalDateTime updatedAt) {
        this.profileId = profileId;
        this.userId = userId;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.city = city;
        this.postalCode = postalCode;
        this.updatedAt = updatedAt;
    }

    public String getProfileId() {return profileId;}
    public String getUserId() {return userId;}
    public String getFullName() {return fullName;}
    public PhoneNumber getPhoneNumber() {return phoneNumber;}
    public Address getAddress() {return address;}
    public String getCity() {return city;}
    public String getPostalCode() {return postalCode;}
    public LocalDateTime getUpdatedAt() {return updatedAt;}
}
