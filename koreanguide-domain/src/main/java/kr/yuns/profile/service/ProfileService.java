package kr.yuns.profile.service;

import org.springframework.http.ResponseEntity;

import kr.yuns.profile.data.dto.request.ChangeAddressRequestDto;
import kr.yuns.profile.data.dto.request.ChangeBrithReqeustDto;
import kr.yuns.profile.data.dto.request.ChangeNearSubwayRequestDto;
import kr.yuns.profile.data.dto.request.ChangePasswordRequestDto;
import kr.yuns.profile.data.dto.request.ChangeProfileNonPasswordRequestDto;
import kr.yuns.profile.data.dto.request.ChangeProfileRequestDto;
import kr.yuns.profile.data.dto.response.MainProfileAlertResponseDto;

public interface ProfileService {
    ResponseEntity<?> getUserProfile(Long userId);
    // ResponseEntity<MainProfileAlertResponseDto> getMainPageProfileAlert(Long userId);
    // ResponseEntity<?> depositMainPageProfileCompleteCredit(Long userId);
    // ResponseEntity<?> getUserInfo(Long userId);
    ResponseEntity<?> changeName(Long userId, ChangeProfileRequestDto changeProfileRequestDto);
    ResponseEntity<?> changePhoneNum(Long userId, ChangeProfileRequestDto changeProfileRequestDto);
    ResponseEntity<?> removeProfileUrl(Long userId);
    ResponseEntity<?> changeProfileUrl(Long userId, ChangeProfileRequestDto changeProfileRequestDto);
    ResponseEntity<?> changePassword(Long userId, ChangePasswordRequestDto changePasswordRequestDto);
    ResponseEntity<?> changeIntroduce(Long userId,
                                      ChangeProfileNonPasswordRequestDto changeProfileNonPasswordRequestDto);
    ResponseEntity<?> changeNickname(Long userId, ChangeProfileRequestDto changeProfileRequestDto);
    // ResponseEntity<?> getMainPageInfo(Long userId);
    ResponseEntity<?> getMyPageInfo(Long userId);
    ResponseEntity<?> changeNearSubway(Long userId, ChangeNearSubwayRequestDto changeNearSubwayRequestDto);
    ResponseEntity<?> changeAddress(Long userId, ChangeAddressRequestDto changeAddressRequestDto);
    ResponseEntity<?> changeBirth(Long userId, ChangeBrithReqeustDto changeBrithReqeustDto);
    // ResponseEntity<?> getInfoBoxInfo(Long userId);
}