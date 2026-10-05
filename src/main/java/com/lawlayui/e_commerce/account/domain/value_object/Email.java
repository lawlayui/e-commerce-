package com.lawlayui.e_commerce.account.domain.value_object;

import java.util.regex.Pattern;

import com.lawlayui.e_commerce.account.domain.exception.InvalidEmailException;

public class Email {
    private String value;

    private Email(String email) {
        this.value = email;
    }

    public static Email of(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern emailPattern = Pattern.compile(emailRegex);

        if (!emailPattern.matcher(email).matches()) {
            throw new InvalidEmailException();
        }

        return new Email(email.toLowerCase().trim());
    }

    public String getValue() {
        return value;
    }
}
