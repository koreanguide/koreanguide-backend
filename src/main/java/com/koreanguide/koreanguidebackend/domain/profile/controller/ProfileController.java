package com.koreanguide.koreanguidebackend.domain.profile.controller;

import com.koreanguide.koreanguidebackend.config.security.JwtTokenProvider;
import com.koreanguide.koreanguidebackend.domain.profile.data.dto.request.*;
import com.koreanguide.koreanguidebackend.domain.profile.service.ProfileService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {
    private final ProfileService profileService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public ProfileController(ProfileService profileService, JwtTokenProvider jwtTokenProvider) {
        this.profileService = profileService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public Long GET_USER_ID_BY_TOKEN(HttpServletRequest request) {
        return jwtTokenProvider.getUserIdByToken(request.getHeader("X-AUTH-TOKEN"));
    }

    @PostMapping("/name")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> changeName(HttpServletRequest request,
                                        @RequestBody ChangeProfileRequestDto changeProfileRequestDto) {
        return profileService.changeName(GET_USER_ID_BY_TOKEN(request), changeProfileRequestDto);
    }

    @PostMapping("/phone")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> changePhoneNum(HttpServletRequest request,
                                            @RequestBody ChangeProfileRequestDto changeProfileRequestDto) {
        return profileService.changePhoneNum(GET_USER_ID_BY_TOKEN(request), changeProfileRequestDto);
    }

    @PostMapping("/profile")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> changeProfileUrl(HttpServletRequest request,
                                              @RequestBody ChangeProfileRequestDto changeProfileRequestDto) {
        return profileService.changeProfileUrl(GET_USER_ID_BY_TOKEN(request), changeProfileRequestDto);
    }

    @DeleteMapping("/profile")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> removeProfileUrl(HttpServletRequest request) {
        return profileService.removeProfileUrl(GET_USER_ID_BY_TOKEN(request));
    }

    @PostMapping("/introduce")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> changeIntroduce(HttpServletRequest request,
                                             @RequestBody ChangeProfileNonPasswordRequestDto changeProfileNonPasswordRequestDto) {
        return profileService.changeIntroduce(GET_USER_ID_BY_TOKEN(request), changeProfileNonPasswordRequestDto);
    }

    @PostMapping("/password")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> changePassword(HttpServletRequest request,
                                            @RequestBody ChangePasswordRequestDto changePasswordRequestDto) {
        return profileService.changePassword(GET_USER_ID_BY_TOKEN(request), changePasswordRequestDto);
    }

    @PostMapping("/nickname")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> changeNickname(HttpServletRequest request,
                                            @RequestBody ChangeProfileRequestDto changeProfileRequestDto) {
        return profileService.changeNickname(GET_USER_ID_BY_TOKEN(request), changeProfileRequestDto);
    }

    @GetMapping("/")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> getUserInfo(HttpServletRequest request) {
        return profileService.getUserInfo(GET_USER_ID_BY_TOKEN(request));
    }

    @GetMapping("/info")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> getUserProfile(HttpServletRequest request) {
        return profileService.getUserProfile(GET_USER_ID_BY_TOKEN(request));
    }

    @GetMapping("/main")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> getMainPageInfo(HttpServletRequest request) {
        return profileService.getMainPageInfo(GET_USER_ID_BY_TOKEN(request));
    }

    @GetMapping("/mypage")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> getMyPageInfo(HttpServletRequest request) {
        return profileService.getMyPageInfo(GET_USER_ID_BY_TOKEN(request));
    }

    @PostMapping("/subway")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> changeNearSubway(HttpServletRequest request,
                                              @RequestBody ChangeNearSubwayRequestDto changeNearSubwayRequestDto) {
        return profileService.changeNearSubway(GET_USER_ID_BY_TOKEN(request), changeNearSubwayRequestDto);
    }

    @PostMapping("/address")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> changeAddress(HttpServletRequest request,
                                           @RequestBody ChangeAddressRequestDto changeAddressRequestDto) {
        return profileService.changeAddress(GET_USER_ID_BY_TOKEN(request), changeAddressRequestDto);
    }

    @PostMapping("/birth")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> changeBirth(HttpServletRequest request,
                                         @RequestBody ChangeBrithReqeustDto changeBrithReqeustDto) {
        return profileService.changeBirth(GET_USER_ID_BY_TOKEN(request), changeBrithReqeustDto);
    }

    @GetMapping("/infobox")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> getInfoBoxInfo(HttpServletRequest request) {
        return profileService.getInfoBoxInfo(GET_USER_ID_BY_TOKEN(request));
    }
}
