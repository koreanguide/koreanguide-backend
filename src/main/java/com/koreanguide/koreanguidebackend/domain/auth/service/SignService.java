package com.koreanguide.koreanguidebackend.domain.auth.service;

import com.koreanguide.koreanguidebackend.domain.auth.data.dto.request.ResetPasswordRequestDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.request.SignUpRequestDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.request.TokenRequestDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.response.SignInResponseDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.request.SignInRequestDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.response.BaseResponseDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.enums.VerifyType;
import org.springframework.http.ResponseEntity;

import javax.mail.MessagingException;

public interface SignService {
    ResponseEntity<?> sendVerifyMail(String to) throws MessagingException;
    boolean validateAuthKey(VerifyType verifyType, String email, String inputKey);
    ResponseEntity<?> signUp(SignUpRequestDto signUpRequestDto);
    ResponseEntity<?> signIn(SignInRequestDto signInRequestDto);
    ResponseEntity<?> resetPassword(ResetPasswordRequestDto resetPasswordRequestDto);
    ResponseEntity<?> sendResetPasswordVerifyMail(String to) throws MessagingException;

    ResponseEntity<?> refreshToken(TokenRequestDto tokenRequestDto);
}
