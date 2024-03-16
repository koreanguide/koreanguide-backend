package com.koreanguide.koreanguidebackend.domain.auth.data.dao;

import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.auth.exception.UserNotFoundException;

public interface UserDao {
    User getUserEntity(Long userId) throws UserNotFoundException;

    User getUserEntityByEmail(String email) throws UserNotFoundException;

    boolean checkAlreadyExistUserByEmail(String email);

    boolean checkAlreadyExistUserByNickname(String nickname);

    void saveUserEntity(User user);

    boolean checkPassword(User user, String password) throws RuntimeException;

    void changePassword(User user, String password);
}
