package com.koreanguide.koreanguidebackend.domain.profile.data.dao;

import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.profile.data.entity.Profile;

public interface ProfileDao {
    Profile getUserProfile(User user);

    void saveProfileEntity(Profile profile);
}
