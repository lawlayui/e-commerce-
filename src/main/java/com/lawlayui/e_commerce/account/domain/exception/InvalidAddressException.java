package com.lawlayui.e_commerce.account.domain.exception;

public class InvalidAddressException extends RuntimeException{
    public InvalidAddressException(String msg) {
        super(msg);
    }
    public InvalidAddressException() {
        super("The address must follow the pattern (street_address, village, subdistrict).");
    }
}
