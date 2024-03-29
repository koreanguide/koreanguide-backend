package com.koreanguide.koreanguidebackend.domain.auth.controller;

import com.koreanguide.koreanguidebackend.domain.auth.data.dto.request.*;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.response.SignAlertResponseDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.response.SignInResponseDto;
import com.koreanguide.koreanguidebackend.domain.auth.service.SignService;
import com.koreanguide.koreanguidebackend.domain.mail.data.enums.MailType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@RequestMapping("/api")
public class SignController {
    private final SignService signService;

    @Autowired
    public SignController(SignService signService) {
        this.signService = signService;
    }

    @ApiOperation(value = "비밀번호 재설정 이메일 인증 번호 요청")
    @PostMapping("/v1/verify/request/pw")
    public ResponseEntity<?> requestResetPasswordVerifyEmail(
            @RequestBody ValidateEmailRequestDto validateEmailRequestDto) throws MessagingException {
        return signService.sendResetPasswordVerifyMail(validateEmailRequestDto.getEmail());
    }

    @ApiOperation(value = "비밀번호 재설정 이메일 인증 번호 확인")
    @PostMapping("/v1/verify/validate/pw")
    public ResponseEntity<SignAlertResponseDto> validateResetPasswordEmail(
            @RequestBody ValidateRequestDto validateRequestDto) {
        return signService.validateKey(
                MailType.RESET_PASSWORD_VERIFY, validateRequestDto.getEmail(), validateRequestDto.getKey());
    }

    @ApiOperation(value = "비밀번호 재설정")
    @PutMapping("/v1/reset/password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequestDto resetPasswordRequestDto) {
        return signService.resetPassword(resetPasswordRequestDto);
    }

    @ApiOperation(value = "회원가입 이메일 인증 번호 요청")
    @PostMapping("/v1/verify/request")
    public ResponseEntity<?> requestEmailAuth(@RequestBody ValidateEmailRequestDto validateEmailRequestDto)
            throws MessagingException {
        return signService.sendVerifyMail(validateEmailRequestDto.getEmail());
    }

    @ApiOperation(value = "[V2] 회원가입 이메일 인증 번호 요청")
    @PostMapping("/v2/verify/request")
    public ResponseEntity<?> requestVerifyMail(@RequestBody ValidateEmailRequestDto validateEmailRequestDto) throws MessagingException {
        return signService.requestVerifyMail(validateEmailRequestDto.getEmail());
    }

    @ApiOperation(value = "회원가입 이메일 인증 번호 확인")
    @PostMapping("/v1/verify/validate")
    public ResponseEntity<?> validateEmail(@RequestBody ValidateRequestDto validateRequestDto) {
        return signService.validateKey(MailType.REGISTER_VERIFY, validateRequestDto.getEmail(),
                validateRequestDto.getKey());
    }

    @ApiOperation(value = "회원가입")
    @PostMapping(value = "/v1/signup")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        return signService.signUp(signUpRequestDto);
    }

    @ApiOperation(value = "카카오 로그인")
    @PostMapping(value = "/v1/kakao")
    public ResponseEntity<SignInResponseDto> signWithKakao(@RequestParam String code) throws Exception {
        return signService.socialKakaoLogin(code);
    }

    @ApiOperation(value = "로그인")
    @PostMapping(value = "/v1/signin")
    public ResponseEntity<?> signIn(@RequestBody SignInRequestDto signInRequestDto) throws RuntimeException {
        return signService.signIn(signInRequestDto);
    }

    @ApiOperation(value = "Refresh Token을 이용해 Access Token 재발급")
    @PostMapping(value = "/v1/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRequestDto tokenRequestDto){
        return signService.refreshToken(tokenRequestDto);
    }

    @ApiOperation(value = "Access Token 유효성 검증")
    @PostMapping(value = "/v1/token")
    public ResponseEntity<?> validateToken(@RequestBody TokenRequestDto tokenRequestDto) {
        return signService.validateToken(tokenRequestDto);
    }

    @ApiOperation(value = "Refresh Token을 이용해 Access Token 재발급")
    @GetMapping(value = "/v1/exception")
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
