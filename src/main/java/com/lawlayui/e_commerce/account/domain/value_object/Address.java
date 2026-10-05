package com.lawlayui.e_commerce.account.domain.value_object;

import com.lawlayui.e_commerce.account.domain.exception.InvalidAddressException;

public class Address {
    private String value;

    private Address(String address) {
        this.value = address;
    }

    public static Address of(String address) {
        String addressPattern = "^([^,]+),\\s*(?:Kel\\.\\s*)?([^,]+),\\s*(?:Kec\\.\\s*)?([^,]+),\\s*([^,]+)$";

        if(!address.matches(addressPattern)) {
            throw new InvalidAddressException();
        };

        return new Address(address);
    }
    public String getValue() {
        return value;
    }
}
