package com.koreanguide.koreanguidebackend.domain.credit.data.repository;

import com.koreanguide.koreanguidebackend.domain.credit.data.entity.CreditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditLogRepository extends JpaRepository<CreditLog, Long> {
}
