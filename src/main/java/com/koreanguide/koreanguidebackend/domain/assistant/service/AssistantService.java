package com.koreanguide.koreanguidebackend.domain.assistant.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.koreanguide.koreanguidebackend.domain.assistant.data.dto.request.AssistantRequestDto;
import org.springframework.http.ResponseEntity;

public interface AssistantService {
    ResponseEntity<?> getAssistantMsg(Long userId, AssistantRequestDto assistantRequestDto) throws JsonProcessingException;
}
