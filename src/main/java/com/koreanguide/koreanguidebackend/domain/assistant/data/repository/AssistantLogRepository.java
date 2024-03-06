package com.koreanguide.koreanguidebackend.domain.assistant.data.repository;

import com.koreanguide.koreanguidebackend.domain.assistant.data.entity.AssistantLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssistantLogRepository extends JpaRepository<AssistantLog, Long> {
}
