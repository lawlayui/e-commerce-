package com.lawlayui.e_commerce.account.application.port.in.command;

public class CreateUserProfileCommand {
    private String userId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String countryCode;
    private String address; 

    public CreateUserProfileCommand(String userId, String firstName, String lastName, String phoneNumber, String countryCode, String address) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.countryCode = countryCode;
        this.address = address;
    }

    public String getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getAddress() {
        return address;
    }
}
