package kr.yuns.profile.data.dao;

import kr.yuns.auth.data.entity.User;
import kr.yuns.profile.data.entity.Profile;

public interface ProfileDao {
    Profile getUserProfile(User user);
    void saveProfileEntity(Profile profile);
}