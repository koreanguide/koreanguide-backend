package com.koreanguide.koreanguidebackend.domain.profile.service;

import com.koreanguide.koreanguidebackend.domain.profile.data.dto.request.ChangePasswordRequestDto;
import com.koreanguide.koreanguidebackend.domain.profile.data.dto.request.ChangeProfileRequestDto;
import org.springframework.http.ResponseEntity;

public interface ProfileService {
    ResponseEntity<?> getUserInfo(Long userId);

    ResponseEntity<?> changeName(Long userId, ChangeProfileRequestDto changeProfileRequestDto);

    ResponseEntity<?> changePhoneNum(Long userId, ChangeProfileRequestDto changeProfileRequestDto);

    ResponseEntity<?> removeProfileUrl(Long userId);

    ResponseEntity<?> changeProfileUrl(Long userId, ChangeProfileRequestDto changeProfileRequestDto);

    ResponseEntity<?> changePassword(Long userId, ChangePasswordRequestDto changePasswordRequestDto);

    ResponseEntity<?> changeIntroduce(Long userId, ChangeProfileRequestDto changeProfileRequestDto);

    ResponseEntity<?> changeNickname(Long userId, ChangeProfileRequestDto changeProfileRequestDto);

    ResponseEntity<?> getMainPageInfo(Long userId);
}
