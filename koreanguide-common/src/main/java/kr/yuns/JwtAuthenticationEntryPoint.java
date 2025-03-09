package kr.yuns;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.yuns.common.CustomResponseEntity;
import kr.yuns.common.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        Object errorObj = request.getAttribute("error");
        Result result = errorObj instanceof Result ? (Result) errorObj : Result.TOKEN_INVALID;
        String jwtToken = request.getHeader("Authorization");
        if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
        //     log.info("JWT Token: {}", jwtToken);
        // } else {
        //     log.info("JWT Token 없음");
        }
        // log.error("url {}, message: {}", request.getRequestURI(), result.getMessage());

        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(objectMapper.writeValueAsString(
                CustomResponseEntity.builder()
                        .code(401)
                        .message(result.getMessage())
                        .build()));
    }
}