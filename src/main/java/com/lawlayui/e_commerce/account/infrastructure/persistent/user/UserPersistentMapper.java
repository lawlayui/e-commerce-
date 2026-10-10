package com.lawlayui.e_commerce.account.infrastructure.persistent.user;

import java.util.List;

import com.lawlayui.e_commerce.account.domain.User;
import com.lawlayui.e_commerce.account.domain.value_object.Role;

public class UserPersistentMapper {
    public static UserEntityJpa toEntity(User user) {
        return UserEntityJpa.builder()
                .userId(user.getUserId() != null ? user.getUserId() : java.util.UUID.randomUUID().toString())
                .email(user.getEmail().getValue())
                .hashedPassword(user.getHashedPassword() != null ? user.getHashedPassword().getHashedValue() : null)
                .role(user.getRole().name())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public static User toDomain(UserEntityJpa entity) {
        return User.rehydrate(
                entity.getUserId(),
                entity.getEmail(),
                entity.getHashedPassword(),
                entity.getRole().equals("SELLER") ? Role.SELLER : Role.CUSTOMER,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public static List<User> toDomainList(List<UserEntityJpa> entities) {
        return entities.stream()
                .map(UserPersistentMapper::toDomain)
                .toList();
    }

    public static List<UserEntityJpa> toEntityList(List<User> users) {
        return users.stream()
                .map(UserPersistentMapper::toEntity)
                .toList();
    }
}
