package com.lawlayui.e_commerce.account.infrastructure.persistent.user_profile;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileJpaRepository extends JpaRepository<UserProfileEntityJpa, String> {
    List<UserProfileEntityJpa> findByUserId(String userId);
    Optional<UserProfileEntityJpa> findByUserProfileIdAndUserId(String userProfileId, String userId);
}
