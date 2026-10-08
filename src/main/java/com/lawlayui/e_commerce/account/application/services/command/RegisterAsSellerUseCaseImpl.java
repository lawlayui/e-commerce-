package com.lawlayui.e_commerce.account.application.services.command;

import com.lawlayui.e_commerce.account.application.event.UserRegisteredAsSeller;
import com.lawlayui.e_commerce.account.application.exception.UserProfileNotFoundException;
import com.lawlayui.e_commerce.account.application.port.out.EventPublisher;
import com.lawlayui.e_commerce.account.application.port.out.UserProfileRepository;
import com.lawlayui.e_commerce.account.domain.entity.UserProfile;

public class RegisterAsSellerUseCaseImpl {
    private UserProfileRepository reposiory;
    private EventPublisher publisher;

    public RegisterAsSellerUseCaseImpl(UserProfileRepository repository, EventPublisher publisher) {
        this.reposiory = repository;
        this.publisher = publisher;
    }

    public void registerAsSeller(String userProfileId, String userId) {
        UserProfile userProfile = reposiory.getByUserProfileIdAndUserId(userProfileId, userId)
            .orElseThrow(() -> new UserProfileNotFoundException(userProfileId, userId));

        reposiory.save(userProfile);
        publisher.publish(new UserRegisteredAsSeller(userId));
    }
}
