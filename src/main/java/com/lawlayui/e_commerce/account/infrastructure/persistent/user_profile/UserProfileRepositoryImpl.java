package com.lawlayui.e_commerce.account.infrastructure.persistent.user_profile;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.lawlayui.e_commerce.account.application.port.out.UserProfileRepository;
import com.lawlayui.e_commerce.account.domain.entity.UserProfile;

@Repository 
public class UserProfileRepositoryImpl implements UserProfileRepository{
    @Override
    public void deleteByUserProfileIdAndUserId(String userProfileId, String userId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Optional<UserProfile> getById(String userProfileId) {
        // TODO Auto-generated method stub
        return Optional.empty();
    }

    @Override
    public List<UserProfile> getByUserId(String userId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<UserProfile> getByUserProfileIdAndUserId(String userProfileId, String userId) {
        // TODO Auto-generated method stub
        return Optional.empty();
    }

    private UserProfileJpaRepository userProfileJpaRepository;

    public UserProfileRepositoryImpl(UserProfileJpaRepository userProfileJpaRepository) {
        this.userProfileJpaRepository = userProfileJpaRepository;
    }

    public void save(UserProfile userProfile) {
        return null;
    }
}
