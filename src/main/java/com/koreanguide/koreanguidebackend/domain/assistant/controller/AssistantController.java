package com.koreanguide.koreanguidebackend.domain.assistant.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.koreanguide.koreanguidebackend.config.security.JwtTokenProvider;
import com.koreanguide.koreanguidebackend.domain.assistant.data.dto.request.AssistantRequestDto;
import com.koreanguide.koreanguidebackend.domain.assistant.service.AssistantService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/gpt")
public class AssistantController {
    private final AssistantService assistantService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AssistantController(AssistantService assistantService, JwtTokenProvider jwtTokenProvider) {
        this.assistantService = assistantService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public Long GET_USER_ID_BY_TOKEN(HttpServletRequest request) {
        return jwtTokenProvider.getUserIdByToken(request.getHeader("X-AUTH-TOKEN"));
    }

    @PostMapping("/")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> getAssistantMsg(HttpServletRequest request,
                                             @RequestBody AssistantRequestDto assistantRequestDto)
            throws JsonProcessingException {
        return assistantService.getAssistantMsg(GET_USER_ID_BY_TOKEN(request), assistantRequestDto);
    }
}
