package com.koreanguide.koreanguidebackend.domain.auth.data.dao.Impl;

import com.koreanguide.koreanguidebackend.domain.auth.data.dao.UserDao;
import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.auth.data.repository.UserRepository;
import com.koreanguide.koreanguidebackend.domain.auth.exception.UserNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserDaoImpl implements UserDao {
    private final UserRepository userRepository;

    public UserDaoImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUserEntity(Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if(user.isEmpty()) {
            throw new UserNotFoundException();
        }

        return user.get();
    }

    @Override
    public void saveUserEntity(User user) {
        userRepository.save(user);
    }
}
