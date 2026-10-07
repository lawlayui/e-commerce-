package com.lawlayui.e_commerce.account.application.port.out;

import java.util.List;
import java.util.Optional;

import com.lawlayui.e_commerce.account.domain.User;

public interface UserRepository {
    void save(User user);
    void deleteById(String userId);
    Optional<User> getById(String userId);
    List<User> getAll(int page, int pageSize);
}
