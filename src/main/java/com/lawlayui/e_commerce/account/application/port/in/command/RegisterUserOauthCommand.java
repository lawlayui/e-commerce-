package com.lawlayui.e_commerce.account.application.port.in.command;

public class RegisterUserOauthCommand {
    public RegisterUserOauthCommand(String email) {
        this.email = email;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    private String email; 
}
