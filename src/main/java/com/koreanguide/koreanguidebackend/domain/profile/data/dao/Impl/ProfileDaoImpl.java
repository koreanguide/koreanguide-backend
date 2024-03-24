package com.koreanguide.koreanguidebackend.domain.profile.data.dao.Impl;

import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.profile.data.dao.ProfileDao;
import com.koreanguide.koreanguidebackend.domain.profile.data.entity.Profile;
import com.koreanguide.koreanguidebackend.domain.profile.repository.ProfileRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ProfileDaoImpl implements ProfileDao {
    private final ProfileRepository profileRepository;

    public ProfileDaoImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public Profile getUserProfile(User user) {
        Optional<Profile> profile = profileRepository.findByUser(user);

        return profile.orElse(null);

    }

    @Override
    public void saveProfileEntity(Profile profile) {
        profileRepository.save(profile);
    }
}
