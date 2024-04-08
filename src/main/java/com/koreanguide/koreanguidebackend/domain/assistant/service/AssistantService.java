package com.koreanguide.koreanguidebackend.domain.assistant.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.koreanguide.koreanguidebackend.domain.assistant.data.dto.request.AssistantRequestDto;
import org.springframework.http.ResponseEntity;

public interface AssistantService {
    JsonNode callChatGpt(String systemContents, String userContents) throws JsonProcessingException;
    void saveAssistantLog(String msg, JsonNode jsonNode, Long userId);
    ResponseEntity<?> getAssistantMsg(Long userId, AssistantRequestDto assistantRequestDto) throws JsonProcessingException;
}
