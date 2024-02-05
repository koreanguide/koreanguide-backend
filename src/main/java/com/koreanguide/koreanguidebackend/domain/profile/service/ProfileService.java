package com.koreanguide.koreanguidebackend.domain.profile.service;

import com.koreanguide.koreanguidebackend.domain.profile.data.dto.request.ChangeProfileRequestDto;
import org.springframework.http.ResponseEntity;

public interface ProfileService {
    ResponseEntity<?> getUserInfo(Long userId);

    ResponseEntity<?> changeName(Long userId, ChangeProfileRequestDto changeProfileRequestDto);

    ResponseEntity<?> changePhoneNum(Long userId, ChangeProfileRequestDto changeProfileRequestDto);

    ResponseEntity<?> changeProfileUrl(Long userId, ChangeProfileRequestDto changeProfileRequestDto);

    ResponseEntity<?> changeIntroduce(Long userId, ChangeProfileRequestDto changeProfileRequestDto);
}
