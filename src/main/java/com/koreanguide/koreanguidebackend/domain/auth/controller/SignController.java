package com.koreanguide.koreanguidebackend.domain.auth.controller;

import com.koreanguide.koreanguidebackend.domain.auth.data.dto.request.*;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.response.SignAlertResponseDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.enums.VerifyType;
import com.koreanguide.koreanguidebackend.domain.auth.service.SignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Tag;
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
@Api(tags = {"Auth API"})
@RestController
@RequestMapping("/api/v1")
public class SignController {
    private final SignService signService;

    @Autowired
    public SignController(SignService signService) {
        this.signService = signService;
    }

    @ApiOperation(value = "비밀번호 재설정 이메일 인증 번호 요청")
    @PostMapping("/verify/request/pw")
    public ResponseEntity<?> requestResetPasswordVerifyEmail(@RequestBody ValidateEmailRequestDto validateEmailRequestDto) throws MessagingException {
        return signService.sendResetPasswordVerifyMail(validateEmailRequestDto.getEmail());
    }

    @ApiOperation(value = "비밀번호 재설정 이메일 인증 번호 확인")
    @PostMapping("/verify/validate/pw")
    public ResponseEntity<?> validateResetPasswordEmail(@RequestBody ValidateRequestDto validateRequestDto) {
        if (signService.validateAuthKey(VerifyType.RESET_PASSWORD, validateRequestDto.getEmail(), validateRequestDto.getKey())) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    SignAlertResponseDto.builder()
                            .ko("이메일 인증 번호가 일치하지 않습니다.")
                            .en("Email authentication number does not match.")
                            .build());
        }
    }

    @ApiOperation(value = "비밀번호 재설정")
    @PutMapping("/reset/password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequestDto resetPasswordRequestDto) {
        return signService.resetPassword(resetPasswordRequestDto);
    }

    @ApiOperation(value = "회원가입 이메일 인증 번호 요청")
    @PostMapping("/verify/request")
    public ResponseEntity<?> requestEmailAuth(@RequestBody ValidateEmailRequestDto validateEmailRequestDto) throws MessagingException {
        return signService.sendVerifyMail(validateEmailRequestDto.getEmail());
    }

    @ApiOperation(value = "회원가입 이메일 인증 번호 확인")
    @PostMapping("/verify/validate")
    public ResponseEntity<?> validateEmail(@RequestBody ValidateRequestDto validateRequestDto) {
        if (signService.validateAuthKey(VerifyType.SIGNUP, validateRequestDto.getEmail(), validateRequestDto.getKey())) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    SignAlertResponseDto.builder()
                            .ko("이메일 인증 번호가 일치하지 않습니다.")
                            .en("Email authentication number does not match.")
                            .build());
        }
    }

    @ApiOperation(value = "회원가입")
    @PostMapping(value = "/signup")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        if (!signService.validateAuthKey(VerifyType.SIGNUP, signUpRequestDto.getEmail(), signUpRequestDto.getAuthKey())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return signService.signUp(signUpRequestDto);
    }

    @ApiOperation(value = "로그인")
    @PostMapping(value = "/signin")
    public ResponseEntity<?> signIn(@RequestBody SignInRequestDto signInRequestDto) throws RuntimeException {
        return signService.signIn(signInRequestDto);
    }

    @ApiOperation(value = "Refresh Token을 이용해 Access Token 재발급")
    @PostMapping(value = "/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRequestDto tokenRequestDto){
        return signService.refreshToken(tokenRequestDto);
    }

    @ApiOperation(value = "Access Token 유효성 검증")
    @PostMapping(value = "/token")
    public ResponseEntity<?> validateToken(@RequestBody TokenRequestDto tokenRequestDto) {
        return signService.validateToken(tokenRequestDto);
    }

    @ApiOperation(value = "Refresh Token을 이용해 Access Token 재발급")
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
