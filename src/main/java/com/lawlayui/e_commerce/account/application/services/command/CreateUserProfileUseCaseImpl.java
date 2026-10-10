package com.lawlayui.e_commerce.account.application.services.command;

import com.lawlayui.e_commerce.account.application.exception.UserNotFoundException;
import com.lawlayui.e_commerce.account.application.mapper.UserProfileMapper;
import com.lawlayui.e_commerce.account.application.port.in.command.CreateUserProfileCommand;
import com.lawlayui.e_commerce.account.application.port.out.UserProfileRepository;
import com.lawlayui.e_commerce.account.application.port.out.UserRepository;
import com.lawlayui.e_commerce.account.domain.entity.UserProfile;

public class CreateUserProfileUseCaseImpl {
    private UserProfileRepository repository;
    private UserRepository userRepository;

    public CreateUserProfileUseCaseImpl(UserProfileRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public void  createUserProfile(CreateUserProfileCommand command) {
        userRepository.getById(command.getUserId()).orElseThrow(() -> new UserNotFoundException(command.getUserId()));

        UserProfile userProfile = UserProfileMapper.commandToDomain(command);

        repository.save(userProfile);
    }
}
