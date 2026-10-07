package com.lawlayui.e_commerce.account.application.services.query;

import com.lawlayui.e_commerce.account.application.exception.UserProfileNotFoundException;
import com.lawlayui.e_commerce.account.application.mapper.UserProfileMapper;
import com.lawlayui.e_commerce.account.application.port.in.dto.UserProfileDto;
import com.lawlayui.e_commerce.account.application.port.out.UserProfileRepository;
import com.lawlayui.e_commerce.account.domain.entity.UserProfile;

public class GetUserProfileByUserProfileIdAndUserIdUseCaseImpl {
    private UserProfileRepository userProfileRepository;

    public GetUserProfileByUserProfileIdAndUserIdUseCaseImpl(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    public UserProfileDto getByUserProfileIdAndUserId(String userProfileId, String userId) {
        UserProfile userProfile = userProfileRepository.getByUserProfileIdAndUserId(userProfileId, userId)  
            .orElseThrow(() -> new UserProfileNotFoundException(userProfileId, userId));

        return UserProfileMapper.toDto(userProfile);
    }
}
