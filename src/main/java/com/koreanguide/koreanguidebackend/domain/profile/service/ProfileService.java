package com.koreanguide.koreanguidebackend.domain.profile.service;

import com.koreanguide.koreanguidebackend.domain.profile.data.dto.request.*;
import org.springframework.http.ResponseEntity;

public interface ProfileService {
    ResponseEntity<?> getUserProfile(Long userId);

    ResponseEntity<?> getUserInfo(Long userId);

    ResponseEntity<?> changeName(Long userId, ChangeProfileRequestDto changeProfileRequestDto);

    ResponseEntity<?> changePhoneNum(Long userId, ChangeProfileRequestDto changeProfileRequestDto);

    ResponseEntity<?> removeProfileUrl(Long userId);

    ResponseEntity<?> changeProfileUrl(Long userId, ChangeProfileRequestDto changeProfileRequestDto);

    ResponseEntity<?> changePassword(Long userId, ChangePasswordRequestDto changePasswordRequestDto);

    ResponseEntity<?> changeIntroduce(Long userId,
                                      ChangeProfileNonPasswordRequestDto changeProfileNonPasswordRequestDto);

    ResponseEntity<?> changeNickname(Long userId, ChangeProfileRequestDto changeProfileRequestDto);

    ResponseEntity<?> getMainPageInfo(Long userId);

    ResponseEntity<?> getMyPageInfo(Long userId);

    ResponseEntity<?> changeNearSubway(Long userId, ChangeNearSubwayRequestDto changeNearSubwayRequestDto);

    ResponseEntity<?> changeAddress(Long userId, ChangeAddressRequestDto changeAddressRequestDto);

    ResponseEntity<?> changeBirth(Long userId, ChangeBrithReqeustDto changeBrithReqeustDto);

    ResponseEntity<?> getInfoBoxInfo(Long userId);
}
