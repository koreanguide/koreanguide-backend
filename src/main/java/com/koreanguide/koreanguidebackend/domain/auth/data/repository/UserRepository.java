package com.koreanguide.koreanguidebackend.domain.auth.data.repository;

import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User getByEmail(String email);
    User findByEmail(String email);
}
