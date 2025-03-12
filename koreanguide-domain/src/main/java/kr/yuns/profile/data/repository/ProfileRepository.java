package kr.yuns.profile.data.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.yuns.auth.data.entity.User;
import kr.yuns.profile.data.entity.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUser(User user);
}
