package com.koreanguide.koreanguidebackend.domain.auth.data.repository;

import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User getByEmail(String email);
    Optional<User> findByEmail(String email);
    Optional<User> findByNickname(String nickname);
}
