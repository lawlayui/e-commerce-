package com.lawlayui.e_commerce.account.domain.value_object;

import java.util.regex.Pattern;

import com.lawlayui.e_commerce.account.domain.exception.InvalidAddressException;

public class Address {
    private String value;

    private Address(String address) {
        this.value = address;
    }

    public static Address of(String address) {
        Pattern addressPattern = Pattern.compile(
        "(?i)^(?:jalan|jl\\.?)\\s+.+,\\s*" +
        "(?:kelurahan|kel\\.?)\\s+.+,\\s*" +
        "(?:kecamatan|kec\\.?)\\s+.+,\\s*" +
        "(?:kota|kabupaten|kab\\.?)\\s+.+,\\s*" +
        ".+,\\s*\\d{5}$"
        );
        
        if(!addressPattern.matcher(address).matches()) {
            throw new InvalidAddressException();
        };

        return new Address(address);
    }
    public String getValue() {
        return value;
    }
}
