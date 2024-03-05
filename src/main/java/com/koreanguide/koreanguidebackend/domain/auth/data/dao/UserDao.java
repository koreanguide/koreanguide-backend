package com.koreanguide.koreanguidebackend.domain.auth.data.dao;

import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;

public interface UserDao {
    User getUserEntity(Long userId);

    void saveUserEntity(User user);

    boolean checkPassword(User user, String password);

    void changePassword(User user, String password);
}
