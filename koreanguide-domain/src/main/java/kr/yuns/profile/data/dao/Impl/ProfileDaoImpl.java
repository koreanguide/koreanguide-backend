package kr.yuns.profile.data.dao.Impl;

import java.util.Optional;
import org.springframework.stereotype.Component;

import kr.yuns.auth.data.entity.User;
import kr.yuns.profile.data.dao.ProfileDao;
import kr.yuns.profile.data.entity.Profile;
import kr.yuns.profile.data.repository.ProfileRepository;

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