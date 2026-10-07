package com.lawlayui.e_commerce.account.application.exception;

public class UserProfileNotFoundException extends RuntimeException{
    public UserProfileNotFoundException(String userProfileId) {
        super("User profile with id " + userProfileId + " not found");
    }

    public UserProfileNotFoundException(String userProfileId, String userId) {
        super("User profile id " + userProfileId + " for user id " + userId + " was not found");
    }
}
