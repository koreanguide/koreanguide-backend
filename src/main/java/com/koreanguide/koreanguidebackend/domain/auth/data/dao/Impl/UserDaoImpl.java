package com.koreanguide.koreanguidebackend.domain.auth.data.dao.Impl;

import com.koreanguide.koreanguidebackend.domain.auth.data.dao.UserDao;
import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.auth.data.repository.UserRepository;
import com.koreanguide.koreanguidebackend.domain.auth.exception.UserNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserDaoImpl implements UserDao {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDaoImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User getUserEntity(Long userId) throws UserNotFoundException {
        Optional<User> user = userRepository.findById(userId);

        if(user.isEmpty()) {
            throw new UserNotFoundException();
        }

        return user.get();
    }

    @Override
    public User getUserEntityByEmail(String email) throws UserNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);

        if(user.isEmpty()) {
            throw new UserNotFoundException();
        }

        return user.get();
    }

    @Override
    public boolean checkAlreadyExistUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.isPresent();
    }

    @Override
    public boolean checkAlreadyExistUserByNickname(String nickname) {
        Optional<User> user = userRepository.findByNickname(nickname);
        return user.isPresent();
    }

    @Override
    public void saveUserEntity(User user) {
        userRepository.save(user);
    }

    @Override
    public boolean checkPassword(User user, String password) {
        return passwordEncoder.matches(password, user.getPassword());
    }

    @Override
    public void changePassword(User user, String password) throws RuntimeException {
        if(passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException();
        }

        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        if(!passwordPattern.matches(passwordPattern)) {
            throw new RuntimeException();
        }

        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }
}
