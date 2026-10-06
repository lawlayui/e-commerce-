package com.lawlayui.e_commerce.account.domain.exception;

public class InvalidHashedPasswordException extends RuntimeException {
    public InvalidHashedPasswordException(String msg) {
        super(msg);
    }

    public InvalidHashedPasswordException() {
        super("Invalid hashed password format.");
    }
}
