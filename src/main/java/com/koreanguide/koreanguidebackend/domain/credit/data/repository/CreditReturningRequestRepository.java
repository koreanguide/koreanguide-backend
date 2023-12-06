package com.koreanguide.koreanguidebackend.domain.credit.data.repository;

import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.credit.data.entity.CreditReturningRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreditReturningRequestRepository extends JpaRepository<CreditReturningRequest, Long> {
    List<CreditReturningRequest> getAllByUser(User user);
}
