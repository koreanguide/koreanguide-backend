package com.koreanguide.koreanguidebackend.domain.auth.data.dao;

import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;

public interface UserDao {
    User getUserEntity(Long userId);
}
