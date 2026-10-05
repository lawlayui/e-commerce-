package com.lawlayui.e_commerce.account.domain.exception;

public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException(String msg) {
        super(msg);
    }

    public InvalidEmailException() {
        super("Invalid email format.");
    }
}
