package com.lawlayui.e_commerce.account.application.exception;

public class UserAlredyRegistedException extends RuntimeException{
    public UserAlredyRegistedException(String email) {
        super("User with email " + email + " alredy registered");
    }
} 
