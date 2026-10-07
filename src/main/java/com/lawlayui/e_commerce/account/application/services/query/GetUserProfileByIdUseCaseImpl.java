package com.lawlayui.e_commerce.account.application.services.query;

import com.lawlayui.e_commerce.account.application.exception.UserProfileNotFoundException;
import com.lawlayui.e_commerce.account.application.mapper.UserProfileMapper;
import com.lawlayui.e_commerce.account.application.port.in.dto.UserProfileDto;
import com.lawlayui.e_commerce.account.application.port.out.UserProfileRepository;
import com.lawlayui.e_commerce.account.domain.entity.UserProfile;

public class GetUserProfileByIdUseCaseImpl {
    private UserProfileRepository userProfileRepository;

    public GetUserProfileByIdUseCaseImpl(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    public UserProfileDto getById(String userProfileId) {
        UserProfile userProfile = userProfileRepository.getById(userProfileId)
            .orElseThrow(() -> new UserProfileNotFoundException(userProfileId));

        return UserProfileMapper.toDto(userProfile);
    }
}
