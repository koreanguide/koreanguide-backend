package com.koreanguide.koreanguidebackend.domain.credit.data.repository;

import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.credit.data.entity.Credit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CreditRepository extends JpaRepository<Credit, Long> {
    Optional<Credit> findByUser(User user);
    Credit getByUser(User user);
}
