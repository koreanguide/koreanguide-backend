package kr.yuns.auth.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.yuns.auth.data.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User getByEmail(String email);
    Optional<User> findByEmail(String email);
    Optional<User> findByNickname(String nickname);
}