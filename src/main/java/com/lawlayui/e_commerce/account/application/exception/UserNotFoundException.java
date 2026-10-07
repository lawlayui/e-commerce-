package com.lawlayui.e_commerce.account.application.exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String userId) {
        super("User with id " + userId + " not found");
    }

    public UserNotFoundException() {
        super("User not found");
    }
}
