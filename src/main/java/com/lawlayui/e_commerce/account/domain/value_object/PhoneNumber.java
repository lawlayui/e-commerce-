package com.lawlayui.e_commerce.account.domain.value_object;

import com.lawlayui.e_commerce.account.domain.exception.InvalidPhoneNumberException;

public class PhoneNumber {
    private String value;

    private PhoneNumber(String phoneNumber) {
        this.value = phoneNumber;
    }

    public static PhoneNumber of(String countryCode, String phoneNumber) {
        String phonePattern = "^08\\d{8,11}$";

        if (!phoneNumber.matches(phonePattern)) {
            throw new InvalidPhoneNumberException();
        }

        return new PhoneNumber(countryCode + " " + phoneNumber);
    }
    public String getValue() {
        return value;
    }
}
