package com.koreanguide.koreanguidebackend.domain.auth.service;

import com.koreanguide.koreanguidebackend.domain.auth.data.dto.request.ResetPasswordRequestDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.request.SignUpRequestDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.request.TokenRequestDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.request.SignInRequestDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.response.SignAlertResponseDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.response.SignInResponseDto;
import com.koreanguide.koreanguidebackend.domain.mail.data.enums.MailType;
import org.springframework.http.ResponseEntity;

import javax.mail.MessagingException;

public interface SignService {
    ResponseEntity<SignInResponseDto> socialKakaoLogin(String code) throws Exception;

    ResponseEntity<SignAlertResponseDto> validateKey(MailType mailType, String targetEmail, String key);
    ResponseEntity<?> sendVerifyMail(String to) throws MessagingException;

    ResponseEntity<?> requestVerifyMail(String to) throws MessagingException;

    ResponseEntity<?> signUp(SignUpRequestDto signUpRequestDto);
    ResponseEntity<?> signIn(SignInRequestDto signInRequestDto);
    ResponseEntity<?> resetPassword(ResetPasswordRequestDto resetPasswordRequestDto);
    ResponseEntity<?> sendResetPasswordVerifyMail(String to) throws MessagingException;
    ResponseEntity<?> refreshToken(TokenRequestDto tokenRequestDto);
    ResponseEntity<?> validateToken(TokenRequestDto tokenRequestDto);
}
