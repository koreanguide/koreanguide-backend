package com.koreanguide.koreanguidebackend.domain.mail.data.repository;

import com.koreanguide.koreanguidebackend.domain.mail.data.entity.MailLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailLogRepository extends JpaRepository<MailLog, Long> {
}
