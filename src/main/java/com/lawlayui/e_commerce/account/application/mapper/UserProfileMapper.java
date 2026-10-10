package com.lawlayui.e_commerce.account.application.mapper;

import java.util.List;

import com.lawlayui.e_commerce.account.application.port.in.command.CreateUserProfileCommand;
import com.lawlayui.e_commerce.account.application.port.in.command.UpdateUserProfileCommand;
import com.lawlayui.e_commerce.account.application.port.in.dto.UserProfileDto;
import com.lawlayui.e_commerce.account.domain.entity.UserProfile;

public class UserProfileMapper {
    public static UserProfileDto toDto(UserProfile userProfile) {
        return new UserProfileDto(userProfile.getProfileId(), 
        userProfile.getFullName(), 
        userProfile.getPhoneNumber().getValue(), 
        userProfile.getAddress().getValue(),
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
    }

    public static UserProfile commandToDomain(CreateUserProfileCommand command) {
        return UserProfile.create(command.getUserId(), command.getFirstName() + " " + command.getLastName(), command.getPhoneNumber(), command.getCountryCode(), command.getAddress());
   }
}
