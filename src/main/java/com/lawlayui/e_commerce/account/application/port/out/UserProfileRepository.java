package com.lawlayui.e_commerce.account.application.port.out;

import java.util.List;
import java.util.Optional;

import com.lawlayui.e_commerce.account.domain.entity.UserProfile;

public interface UserProfileRepository {
    void save(UserProfile userProfile);    
    void deleteByUserProfileIdAndUserId(String userProfileId, String userId);
    Optional<UserProfile> getById(String userProfileId);
    List<UserProfile> getByUserId(String userId);
    Optional<UserProfile> getByUserProfileIdAndUserId(String userProfileId, String userId);
}
