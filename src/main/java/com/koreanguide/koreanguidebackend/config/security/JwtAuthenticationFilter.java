package com.koreanguide.koreanguidebackend.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final List<String> permitUrls = Arrays.asList(
            "/api/signin",
            "/api/signup",
            "/api/refresh",
            "/v2/api-docs",
            "/swagger-resources/**",
            "/swagger-ui.html", "/webjars/**",
            "/swagger/**",
            "/api/exception"
    );


    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return permitUrls.stream()
                .anyMatch(url -> new AntPathMatcher().match(url, request.getServletPath()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest servletRequest,
                                    HttpServletResponse servletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = jwtTokenProvider.resolveToken(servletRequest);
        log.info("JwtAuthenticationFilter - doFilterInternal(): Token 추출 완료. Token: {}", token);

        log.info("JwtAuthenticationFilter - doFilterInternal(): Token 유효성 체크 시작");
        if (token != null && jwtTokenProvider.validateToken(token)) {
            try {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("JwtAuthenticationFilter - doFilterInternal(): Token 유효성 체크 완료");
            } catch (RuntimeException e) {
                log.error("JwtAuthenticationFilter - doFilterInternal(): 오류 발생 됨.");
                throw new RuntimeException("유효하지 않은 접근 토큰입니다.");
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
