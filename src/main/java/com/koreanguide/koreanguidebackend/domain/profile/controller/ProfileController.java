package com.koreanguide.koreanguidebackend.domain.profile.controller;

import com.koreanguide.koreanguidebackend.config.security.JwtTokenProvider;
import com.koreanguide.koreanguidebackend.domain.credit.data.enums.TransactionContent;
import com.koreanguide.koreanguidebackend.domain.credit.service.CreditService;
import com.koreanguide.koreanguidebackend.domain.profile.data.dto.request.*;
import com.koreanguide.koreanguidebackend.domain.profile.data.dto.response.MainProfileAlertResponseDto;
import com.koreanguide.koreanguidebackend.domain.profile.service.ProfileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Api(tags = {"Profile API"})
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/profile")
public class ProfileController {
    private final ProfileService profileService;
    private final JwtTokenProvider jwtTokenProvider;

    public Long GET_USER_ID_BY_TOKEN(HttpServletRequest request) {
        return jwtTokenProvider.getUserIdByToken(request.getHeader("X-AUTH-TOKEN"));
    }

    @ApiOperation(value = "사용자 이름(실명) 변경")
    @PostMapping("/name")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> changeName(HttpServletRequest request,
                                        @RequestBody ChangeProfileRequestDto changeProfileRequestDto) {
        return profileService.changeName(GET_USER_ID_BY_TOKEN(request), changeProfileRequestDto);
    }

    @ApiOperation(value = "사용자 전화번호 변경")
    @PostMapping("/phone")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> changePhoneNum(HttpServletRequest request,
                                            @RequestBody ChangeProfileRequestDto changeProfileRequestDto) {
        return profileService.changePhoneNum(GET_USER_ID_BY_TOKEN(request), changeProfileRequestDto);
    }

    @ApiOperation(value = "사용자 프로필 사진 변경")
    @PostMapping("/profile")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> changeProfileUrl(HttpServletRequest request,
                                              @RequestBody ChangeProfileRequestDto changeProfileRequestDto) {
        return profileService.changeProfileUrl(GET_USER_ID_BY_TOKEN(request), changeProfileRequestDto);
    }

    @ApiOperation(value = "메인 페이지 프로필 완성 단계")
    @GetMapping("/progress")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public ResponseEntity<MainProfileAlertResponseDto> getMainPageProfileAlert(HttpServletRequest request) {
        return profileService.getMainPageProfileAlert(GET_USER_ID_BY_TOKEN(request));
    }

    @ApiOperation(value = "메인 페이지 프로필 완성 단계 크레딧 지급 요청")
    @PostMapping("/progress/deposit")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> depositMainPageProfileCompleteCredit(HttpServletRequest request) {
        return profileService.depositMainPageProfileCompleteCredit(GET_USER_ID_BY_TOKEN(request));
    }

    @ApiOperation(value = "사용자 프로필 사진 삭제")
    @DeleteMapping("/profile")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> removeProfileUrl(HttpServletRequest request) {
        return profileService.removeProfileUrl(GET_USER_ID_BY_TOKEN(request));
    }

    @ApiOperation(value = "사용자 프로필 소개글 변경")
    @PostMapping("/introduce")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> changeIntroduce(HttpServletRequest request,
                                             @RequestBody ChangeProfileNonPasswordRequestDto changeProfileNonPasswordRequestDto) {
        return profileService.changeIntroduce(GET_USER_ID_BY_TOKEN(request), changeProfileNonPasswordRequestDto);
    }

    @ApiOperation(value = "사용자 비밀번호 변경")
    @PostMapping("/password")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> changePassword(HttpServletRequest request,
                                            @RequestBody ChangePasswordRequestDto changePasswordRequestDto) {
        return profileService.changePassword(GET_USER_ID_BY_TOKEN(request), changePasswordRequestDto);
    }

    @ApiOperation(value = "사용자 닉네임 변경")
    @PostMapping("/nickname")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> changeNickname(HttpServletRequest request,
                                            @RequestBody ChangeProfileRequestDto changeProfileRequestDto) {
        return profileService.changeNickname(GET_USER_ID_BY_TOKEN(request), changeProfileRequestDto);
    }

    @ApiOperation(value = "사용자 정보 조회")
    @GetMapping("/")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> getUserInfo(HttpServletRequest request) {
        return profileService.getUserInfo(GET_USER_ID_BY_TOKEN(request));
    }

    @ApiOperation(value = "사용자 프로필 조회")
    @GetMapping("/info")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> getUserProfile(HttpServletRequest request) {
        return profileService.getUserProfile(GET_USER_ID_BY_TOKEN(request));
    }

    @ApiOperation(value = "메인 페이지 정보 조회")
    @GetMapping("/main")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> getMainPageInfo(HttpServletRequest request) {
        return profileService.getMainPageInfo(GET_USER_ID_BY_TOKEN(request));
    }

    @ApiOperation(value = "마이 페이지 정보 조회")
    @GetMapping("/mypage")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> getMyPageInfo(HttpServletRequest request) {
        return profileService.getMyPageInfo(GET_USER_ID_BY_TOKEN(request));
    }

    @ApiOperation(value = "사용자 근처 지하철 역 변경")
    @PostMapping("/subway")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> changeNearSubway(HttpServletRequest request,
                                              @RequestBody ChangeNearSubwayRequestDto changeNearSubwayRequestDto) {
        return profileService.changeNearSubway(GET_USER_ID_BY_TOKEN(request), changeNearSubwayRequestDto);
    }

    @ApiOperation(value = "사용자 주소지 변경")
    @PostMapping("/address")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> changeAddress(HttpServletRequest request,
                                           @RequestBody ChangeAddressRequestDto changeAddressRequestDto) {
        return profileService.changeAddress(GET_USER_ID_BY_TOKEN(request), changeAddressRequestDto);
    }

    @ApiOperation(value = "사용자 생년월일 변경")
    @PostMapping("/birth")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> changeBirth(HttpServletRequest request,
                                         @RequestBody ChangeBrithReqeustDto changeBrithReqeustDto) {
        return profileService.changeBirth(GET_USER_ID_BY_TOKEN(request), changeBrithReqeustDto);
    }

    @ApiOperation(value = "헤더 인포 박스 정보 조회")
    @GetMapping("/infobox")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> getInfoBoxInfo(HttpServletRequest request) {
        return profileService.getInfoBoxInfo(GET_USER_ID_BY_TOKEN(request));
    }
}
