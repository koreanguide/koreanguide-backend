package com.koreanguide.koreanguidebackend.domain.auth.service;

import com.koreanguide.koreanguidebackend.domain.auth.data.dto.request.SignUpRequestDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.response.SignInResponseDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.request.SignInRequestDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.response.BaseResponseDto;
import org.springframework.http.ResponseEntity;

public interface SignService {
    void sendVerifyMail(String to);

    boolean validateAuthKey(String email, String inputKey);

    ResponseEntity<?> signUp(SignUpRequestDto signUpRequestDto);
    SignInResponseDto signIn(SignInRequestDto signInRequestDto);
}
