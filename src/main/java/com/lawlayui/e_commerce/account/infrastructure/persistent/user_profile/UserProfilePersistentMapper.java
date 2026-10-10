package com.lawlayui.e_commerce.account.infrastructure.persistent.user_profile;

import com.lawlayui.e_commerce.account.domain.entity.UserProfile;
import com.lawlayui.e_commerce.account.infrastructure.persistent.user.UserEntityJpa;

public class UserProfilePersistentMapper{
    public static UserProfileEntityJpa toEntity(UserProfile userProfile, UserEntityJpa user) {
        return UserProfileEntityJpa.builder()
                .profileId(userProfile.getProfileId())
                .user(user)
                .fullName(userProfile.getFullName())
                .phoneNumber(userProfile.getPhoneNumber().getValue())
                .address(userProfile.getAddress().getValue())
                .build();
    }

    public static UserProfile toDomain(UserProfileEntityJpa entity) {
        return UserProfile.reconstitue(
            entity.getProfileId(),
            entity.getUser().getUserId(),
            entity.getFullName(),
            entity.getPhoneNumber(),
            null,
            entity.getAddress(),
            entity.getUpdatedAt()
        );
    }
}
