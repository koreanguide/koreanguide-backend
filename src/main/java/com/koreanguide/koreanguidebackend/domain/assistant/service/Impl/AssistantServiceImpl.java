package com.koreanguide.koreanguidebackend.domain.assistant.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.koreanguide.koreanguidebackend.domain.assistant.data.dao.AssistantDao;
import com.koreanguide.koreanguidebackend.domain.assistant.data.dto.request.AssistantRequestDto;
import com.koreanguide.koreanguidebackend.domain.assistant.data.dto.response.AssistantResponseDto;
import com.koreanguide.koreanguidebackend.domain.assistant.data.entity.AssistantLog;
import com.koreanguide.koreanguidebackend.domain.assistant.service.AssistantService;
import com.koreanguide.koreanguidebackend.domain.auth.data.dao.UserDao;
import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class AssistantServiceImpl implements AssistantService {
    @Value("${openai.api.key}")
    private String apiKey;

    private final AssistantDao assistantDao;
    private final UserDao userDao;

    public AssistantServiceImpl(AssistantDao assistantDao, UserDao userDao) {
        this.assistantDao = assistantDao;
        this.userDao = userDao;
    }

    public JsonNode callChatGpt(String userMsg) throws JsonProcessingException {
        final String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("model", "gpt-4");

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", userMsg);
        messages.add(userMessage);

        Map<String, String> assistantMessage = new HashMap<>();
        assistantMessage.put("role", "system");
        assistantMessage.put("content", "이 서비스는 한국에 방문하는 외국인과 실제 한국인이 만나서 한국에 대해" +
                " 깊이 소개해줄 수 있는 매칭 서비스야." +
                "너는 사용자가 요청한 대로 외국인이 이해하기 쉽게 소개 글을 적어줘야 해" +
                "그리고 기본적으로 한국어로 적고, 사용자가 요청할 때만 영어로 적어줘");
        messages.add(assistantMessage);

        bodyMap.put("messages", messages);

        String body = objectMapper.writeValueAsString(bodyMap);

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        return objectMapper.readTree(response.getBody());
    }

    @Override
    public ResponseEntity<?> getAssistantMsg(Long userId, AssistantRequestDto assistantRequestDto) throws JsonProcessingException {
        User user = userDao.getUserEntity(userId);
        JsonNode jsonNode = callChatGpt(assistantRequestDto.getMsg());
        String GPT_ANSWER = jsonNode.path("choices").get(0).path("message").path("content").asText();

        assistantDao.saveAssistantLogEntity(AssistantLog.builder()
                        .question(assistantRequestDto.getMsg())
                        .answer(GPT_ANSWER)
                        .promptTokens(Long.valueOf(jsonNode.path("usage").path("prompt_tokens").asText()))
                        .completionTokens(Long.valueOf(jsonNode.path("usage").path("completion_tokens").asText()))
                        .totalTokens(Long.valueOf(jsonNode.path("usage").path("total_tokens").asText()))
                        .usedAt(LocalDateTime.now())
                        .user(user)
                .build());

        return ResponseEntity.status(HttpStatus.OK).body(AssistantResponseDto.builder()
                        .msg(GPT_ANSWER)
                .build());
    }
}
