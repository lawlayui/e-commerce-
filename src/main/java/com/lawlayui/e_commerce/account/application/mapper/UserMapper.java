package com.lawlayui.e_commerce.account.application.mapper;

import java.util.List;

import com.lawlayui.e_commerce.account.application.port.in.dto.UserDto;
import com.lawlayui.e_commerce.account.domain.User;

public class UserMapper {
    public static UserDto toDto(User user) {
        return new UserDto(
            user.getUserId(), 
            user.getEmail().getValue(),
            user.getRole().name(), 
            user.isActive(), 
            user.getCreatedAt(), 
            user.getUpdatedAt());
    }

    public static List<UserDto> toDtos(List<User> user) {
        return user.stream()
            .map(UserMapper::toDto)
            .toList();
    }
}
