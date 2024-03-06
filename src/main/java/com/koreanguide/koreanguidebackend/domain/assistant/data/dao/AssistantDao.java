package com.koreanguide.koreanguidebackend.domain.assistant.data.dao;

import com.koreanguide.koreanguidebackend.domain.assistant.data.entity.AssistantLog;

public interface AssistantDao {
    void saveAssistantLogEntity(AssistantLog assistantLog);
}
