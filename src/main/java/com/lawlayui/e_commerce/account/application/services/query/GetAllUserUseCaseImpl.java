package com.lawlayui.e_commerce.account.application.services.query;

import java.util.List;

import com.lawlayui.e_commerce.account.application.mapper.UserMapper;
import com.lawlayui.e_commerce.account.application.port.in.dto.UserDto;
import com.lawlayui.e_commerce.account.application.port.in.query.GetAllUserQuery;
import com.lawlayui.e_commerce.account.application.port.out.UserRepository;

public class GetAllUserUseCaseImpl {
    private UserRepository userRepository;

    public GetAllUserUseCaseImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDto> getAll(GetAllUserQuery query) {
        return UserMapper.toDtos(userRepository.getAll(query.getPage(), query.getPageSize()));
    }
}
