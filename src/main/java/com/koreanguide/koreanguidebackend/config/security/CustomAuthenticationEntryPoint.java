package com.koreanguide.koreanguidebackend.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.EntryPointErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        log.info("CustomAuthenticationEntryPoint - commence(): 사용자 인증 실패");

        EntryPointErrorResponseDto entryPointErrorResponse = new EntryPointErrorResponseDto();
        entryPointErrorResponse.setMsg("사용자 인증을 할 수 없거나, 접근이 허용되지 않은 사용자입니다.");

        httpServletResponse.setStatus(401);
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setCharacterEncoding("utf-8");
        httpServletResponse.getWriter().write(objectMapper.writeValueAsString(entryPointErrorResponse));
    }
}
