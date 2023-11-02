package com.koreanguide.koreanguidebackend.domain.auth.controller;

import com.koreanguide.koreanguidebackend.config.security.JwtTokenProvider;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.*;
import com.koreanguide.koreanguidebackend.domain.auth.service.SignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class SignController {
    private final SignService signService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public SignController(SignService signService, JwtTokenProvider jwtTokenProvider) {
        this.signService = signService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping(value = "/signup")
    public BaseResponseDto signUp(@RequestBody SignRequestDto signRequestDto) {
        return signService.signUp(signRequestDto);
    }

    @PostMapping(value = "/signin")
    public SignInResponseDto signIn(@RequestBody SignRequestDto signRequestDto) throws RuntimeException {
        log.info("SignController - SignIn: 로그인 시도, email: {}", signRequestDto.getEmail());
        SignInResponseDto signInResponseDto = signService.signIn(signRequestDto);

        if (signInResponseDto.isSuccess()) {
            log.info("SignController - signIn: 로그인 성공, email: {}, token: {}",
                    signRequestDto.getEmail(),
                    signInResponseDto.getAccessToken()
            );
        }
        return signInResponseDto;
    }

    @PostMapping(value = "/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRequestDto tokenRequestDto){
        String accessToken = jwtTokenProvider.refreshToken(tokenRequestDto.getRefreshToken(),
                jwtTokenProvider.getUserEmail(tokenRequestDto.getRefreshToken()));
        return ResponseEntity.ok(new TokenResponseDto(accessToken));
    }

    @GetMapping(value = "/exception")
    public void exceptionTest() throws RuntimeException{
        throw new RuntimeException("접근이 금지되었습니다.");
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<Map<String, String>> ExceptionHandler(RuntimeException e){
        HttpHeaders responseHeaders = new HttpHeaders();
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        log.info("ExceptionHandler 호출, {}, {}", e.getCause(), e.getMessage());
        Map<String, String> map = new HashMap<>();
        map.put("ERROR_TYPE", httpStatus.getReasonPhrase());
        map.put("ERROR_CODE", "400");
        map.put("ERROR_MSG", e.getMessage());

        return new ResponseEntity<>(map, responseHeaders,httpStatus);
    }
}
