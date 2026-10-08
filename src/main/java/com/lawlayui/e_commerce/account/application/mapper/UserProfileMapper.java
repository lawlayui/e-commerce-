package com.lawlayui.e_commerce.account.application.mapper;

import java.util.List;

import com.lawlayui.e_commerce.account.application.port.in.command.UpdateUserProfileCommand;
import com.lawlayui.e_commerce.account.application.port.in.dto.UserProfileDto;
import com.lawlayui.e_commerce.account.domain.entity.UserProfile;

public class UserProfileMapper {
    public static UserProfileDto toDto(UserProfile userProfile) {
        return new UserProfileDto(userProfile.getProfileId(), 
        userProfile.getFullName(), 
        userProfile.getPhoneNumber().getValue(), 
        userProfile.getAddress().getValue(), 
        userProfile.getCity(), 
        userProfile.getPostalCode(), 
        userProfile.getUpdatedAt());
    }

    public static List<UserProfileDto> toDtos(List<UserProfile> userProfiles) {
        return userProfiles.stream()
            .map(UserProfileMapper::toDto)
            .toList();
    }

    public static void updateUserProfile(UserProfile profile, UpdateUserProfileCommand command) {
        if (command.getFullName() != null) {
            profile.rename(command.getFullName());
        }
        if (command.getPhoneNumber() != null && command.getCountryCode() != null) {
            profile.updateContactNumber(command.getCountryCode(), command.getPhoneNumber());
        }
        if (command.getAddress() != null && command.getCity() != null && command.getPostalCode() != null) {
            profile.relocateTo(command.getAddress(), command.getCity(), command.getPostalCode());
        }
    }
}
