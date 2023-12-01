package com.koreanguide.koreanguidebackend.domain.credit.data.repository;

import com.koreanguide.koreanguidebackend.domain.credit.data.entity.Credit;
import com.koreanguide.koreanguidebackend.domain.credit.data.entity.CreditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreditLogRepository extends JpaRepository<CreditLog, Long> {
    List<CreditLog> findAllByCredit(Credit credit);
}