package com.lawlayui.e_commerce.account.application.port.in.command;

public class UpdateUserProfileCommand {
    private String userProfileId;
    private String userId;
    private String fullName; 
    private String countryCode;
    private String phoneNumber; 
    private String address; 
    private String city; 
    private String postalCode; 

    public UpdateUserProfileCommand(String userProfileId, String userId, String fullName, String countryCode, String phoneNumber, String address, String city, String postalCode) {
        this.userProfileId = userProfileId;
        this.userId = userId;
        this.fullName = fullName; 
        this.countryCode = countryCode;
        this.phoneNumber = phoneNumber; 
        this.address = address; 
        this.city = city; 
        this.postalCode = postalCode;
    }

    public void setUserProfileId(String userProfileId) {this.userProfileId = userProfileId;}
    public String getUserProfileId() {return userProfileId;}
    public void setUserId(String userId) {this.userId = userId;}
    public String getUserId() {return userId;}
    public void setFullName(String fullName) {this.fullName = fullName;}
    public String getFullName() {return fullName;}
    public void setCountryCode(String countryCode) {this.countryCode = countryCode;}
    public String getCountryCode(){return countryCode;}
    public void setPhoneNumber(String phoneNumber) {this.phoneNumber = phoneNumber;}
    public String getPhoneNumber() {return phoneNumber;}
    public void setAddress(String address) {this.address = address;}
    public String getAddress() {return address;}
    public void setCity(String city) {this.city = city;}
    public String getCity() {return city;}
    public void setPostalCode(String postalCode) {this.postalCode = postalCode;}
    public String getPostalCode() {return postalCode;}
}