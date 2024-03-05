package com.koreanguide.koreanguidebackend.domain.profile.data.dao.Impl;

import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.profile.data.dao.ProfileDao;
import com.koreanguide.koreanguidebackend.domain.profile.data.dto.enums.Language;
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

        return profile.orElseGet(() -> profileRepository.save(Profile.builder()
                .isPublic(true)
                .introduce(null)
                .phoneNum(null)
                .firstLang(Language.KOREAN)
                .secondLang(Language.ENGLISH)
                .subwayLine(null)
                .subwayStation(null)
                .birth(null)
                .name(null)
                .user(user)
                .build()));
    }

    @Override
    public void saveProfileEntity(Profile profile) {
        profileRepository.save(profile);
    }
}
