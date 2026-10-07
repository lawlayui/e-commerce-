package com.lawlayui.e_commerce.account.application.services.query;

import java.util.List;

import com.lawlayui.e_commerce.account.application.exception.UserNotFoundException;
import com.lawlayui.e_commerce.account.application.mapper.UserMapper;
import com.lawlayui.e_commerce.account.application.mapper.UserProfileMapper;
import com.lawlayui.e_commerce.account.application.port.in.dto.UserDto;
import com.lawlayui.e_commerce.account.application.port.in.dto.UserProfileDto;
import com.lawlayui.e_commerce.account.application.port.out.UserProfileRepository;
import com.lawlayui.e_commerce.account.application.port.out.UserRepository;

public class GetUserDetailUseCaseImpl {
    private UserRepository userRepository; 
    private UserProfileRepository userProfileRepository;

    public GetUserDetailUseCaseImpl(UserRepository userRepository, UserProfileRepository userProfileRepository) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
    }

    public UserDto getUserDetail(String userId) {
        UserDto user = UserMapper.toDto(userRepository.getById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId)));

        List<UserProfileDto> userProfileDtos = UserProfileMapper.toDtos(userProfileRepository.getByUserId(userId));
        user.setUserProfiles(userProfileDtos);
        return user;
    }
}

