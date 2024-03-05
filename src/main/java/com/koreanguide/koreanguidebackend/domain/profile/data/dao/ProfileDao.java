package com.koreanguide.koreanguidebackend.domain.profile.data.dao;

import com.koreanguide.koreanguidebackend.domain.profile.data.entity.Profile;

public interface ProfileDao {
    Profile getUserProfile(Long userId);

    void saveProfileEntity(Profile profile);
}
