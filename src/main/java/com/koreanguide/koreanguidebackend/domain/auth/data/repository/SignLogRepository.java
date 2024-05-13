package com.koreanguide.koreanguidebackend.domain.auth.data.repository;

import com.koreanguide.koreanguidebackend.domain.auth.data.entity.SignLog;
import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SignLogRepository extends JpaRepository<SignLog, Long> {
    List<SignLog> getAllByUser(User user);
}
