package com.lawlayui.e_commerce.account.infrastructure.persistent.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import com.lawlayui.e_commerce.account.application.port.out.UserRepository;
import com.lawlayui.e_commerce.account.domain.User;

@Repository 
public class UserRepositoryImpl implements UserRepository {
    private final UserEntityJpaRepository userEntityJpaRepository;

    public UserRepositoryImpl(UserEntityJpaRepository userEntityJpaRepository) {
        this.userEntityJpaRepository = userEntityJpaRepository;
    }

    @Override
    public void save(User user) {
        userEntityJpaRepository.save(UserPersistentMapper.toEntity(user));
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return userEntityJpaRepository.findByEmail(email)
                .map(UserPersistentMapper::toDomain);
    }

    @Override 
    public Optional<User> getById(String userId) {
        return userEntityJpaRepository.findById(userId)
                .map(UserPersistentMapper::toDomain);
    }

    @Override 
    public void deleteById(String userId) {
        userEntityJpaRepository.deleteById(userId);
    }

    @Override 
    public List<User> getAll(int page, int pageSize) {
        return UserPersistentMapper.toDomainList(userEntityJpaRepository.findAll(PageRequest.of(page, pageSize)).getContent());
    }
}
