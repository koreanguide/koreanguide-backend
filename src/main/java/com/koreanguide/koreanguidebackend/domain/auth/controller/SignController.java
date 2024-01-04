package com.koreanguide.koreanguidebackend.domain.auth.controller;

import com.koreanguide.koreanguidebackend.config.security.JwtTokenProvider;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.request.ResetPasswordRequestDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.request.SignInRequestDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.request.SignUpRequestDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.request.TokenRequestDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.response.BaseResponseDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.response.SignAlertResponseDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.response.SignInResponseDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.response.TokenResponseDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.enums.VerifyType;
import com.koreanguide.koreanguidebackend.domain.auth.service.SignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class SignController {
    private final SignService signService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public SignController(SignService signService, JwtTokenProvider jwtTokenProvider) {
        this.signService = signService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/verify/request/pw")
    public ResponseEntity<?> requestResetPasswordVerifyEmail(@RequestParam String email) throws MessagingException {
        return signService.sendResetPasswordVerifyMail(email);
    }

    @PostMapping("/verify/validate/pw")
    public ResponseEntity<?> validateResetPasswordEmail(@RequestParam String email, String key) {
        if (signService.validateAuthKey(VerifyType.RESET_PASSWORD, email, key)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    SignAlertResponseDto.builder()
                            .ko("이메일 인증 번호가 일치하지 않습니다.")
                            .en("Email authentication number does not match.")
                            .build());
        }
    }

    @PutMapping("/reset/password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequestDto resetPasswordRequestDto) {
        return signService.resetPassword(resetPasswordRequestDto);
    }

    @PostMapping("/verify/request")
    public ResponseEntity<?> requestEmailAuth(@RequestParam String email) throws MessagingException {
        return signService.sendVerifyMail(email);
    }

    @PostMapping("/verify/validate")
    public ResponseEntity<?> validateEmail(@RequestParam String email, String authKey) {
        if (signService.validateAuthKey(VerifyType.SIGNUP, email, authKey)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    SignAlertResponseDto.builder()
                            .ko("이메일 인증 번호가 일치하지 않습니다.")
                            .en("Email authentication number does not match.")
                            .build());
        }
    }

    @PostMapping(value = "/signup")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        if (!signService.validateAuthKey(VerifyType.SIGNUP, signUpRequestDto.getEmail(), signUpRequestDto.getAuthKey())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return signService.signUp(signUpRequestDto);
    }

    @PostMapping(value = "/signin")
    public ResponseEntity<?> signIn(@RequestBody SignInRequestDto signInRequestDto) throws RuntimeException {
        return signService.signIn(signInRequestDto);
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
