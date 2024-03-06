package com.koreanguide.koreanguidebackend.domain.assistant.data.dao.Impl;

import com.koreanguide.koreanguidebackend.domain.assistant.data.dao.AssistantDao;
import com.koreanguide.koreanguidebackend.domain.assistant.data.entity.AssistantLog;
import com.koreanguide.koreanguidebackend.domain.assistant.data.repository.AssistantLogRepository;
import org.springframework.stereotype.Component;

@Component
public class AssistantDaoImpl implements AssistantDao {
    private final AssistantLogRepository assistantLogRepository;

    public AssistantDaoImpl(AssistantLogRepository assistantLogRepository) {
        this.assistantLogRepository = assistantLogRepository;
    }

    @Override
    public void saveAssistantLogEntity(AssistantLog assistantLog) {
        assistantLogRepository.save(assistantLog);
    }
}
