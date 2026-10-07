package com.lawlayui.e_commerce.account.application.services.query;

import com.lawlayui.e_commerce.account.application.exception.UserNotFoundException;
import com.lawlayui.e_commerce.account.application.mapper.UserMapper;
import com.lawlayui.e_commerce.account.application.port.in.dto.UserDto;
import com.lawlayui.e_commerce.account.application.port.out.UserRepository;
import com.lawlayui.e_commerce.account.domain.User;

public class GetUserByIdUseCaseImpl {
    private UserRepository userRepository;

    public GetUserByIdUseCaseImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto getById(String userId) {
        User user = userRepository.getById(userId)  
            .orElseThrow(() -> new UserNotFoundException(userId));

        return UserMapper.toDto(user);
    }
}
