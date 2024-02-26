package com.koreanguide.koreanguidebackend.domain.gpt.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.koreanguide.koreanguidebackend.domain.gpt.data.AssistantRequestDto;
import org.springframework.http.ResponseEntity;

public interface GptService {
    ResponseEntity<?> getAssistantMsg(AssistantRequestDto assistantRequestDto) throws JsonProcessingException;
}
