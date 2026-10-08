package com.lawlayui.e_commerce.account.application.services.command;

import com.lawlayui.e_commerce.account.application.exception.UserAlredyRegistedException;
import com.lawlayui.e_commerce.account.application.port.in.command.RegisterUserOauthCommand;
import com.lawlayui.e_commerce.account.application.port.out.UserRepository;
import com.lawlayui.e_commerce.account.domain.User;
import com.lawlayui.e_commerce.account.domain.value_object.Role;

public class RegisterUserOauthUseCaseImpl {
    private UserRepository repository;
    
    public RegisterUserOauthUseCaseImpl(UserRepository repository) {
        this.repository = repository;
    }

    public void registerUser(RegisterUserOauthCommand command) {
        repository.getByEmail(command.getEmail())
            .ifPresent((user) -> {throw new UserAlredyRegistedException(command.getEmail());});
        
        User user = User.register(
            command.getEmail(),
            null,
            Role.CUSTOMER);
        
        repository.save(user);
    }
}
