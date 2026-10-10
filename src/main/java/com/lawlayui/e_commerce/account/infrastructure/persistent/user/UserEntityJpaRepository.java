package com.lawlayui.e_commerce.account.infrastructure.persistent.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserEntityJpaRepository extends JpaRepository<UserEntityJpa, String> {
    Optional<UserEntityJpa> findByEmail(String email);
}
