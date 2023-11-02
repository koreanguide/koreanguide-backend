package com.koreanguide.koreanguidebackend.domain.auth.service;

import com.koreanguide.koreanguidebackend.domain.auth.data.dto.SignInResponseDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.SignRequestDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.BaseResponseDto;

public interface SignService {
    BaseResponseDto signUp(SignRequestDto signRequestDto);
    SignInResponseDto signIn(SignRequestDto signRequestDto);
}
