package com.lawlayui.e_commerce.account.domain.exception;

public class InvalidPhoneNumberException extends RuntimeException{
    public InvalidPhoneNumberException(String msg) {
        super(msg);
    }
    
    public InvalidPhoneNumberException() {
        super("Invalid phone number; it must start with \"08\" and be 8–11 characters long.");
    }
}
