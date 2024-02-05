package com.koreanguide.koreanguidebackend.domain.profile.controller;

import com.koreanguide.koreanguidebackend.config.security.JwtTokenProvider;
import com.koreanguide.koreanguidebackend.domain.profile.data.dto.request.ChangeProfileRequestDto;
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

    @PostMapping("/introduce")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> changeIntroduce(HttpServletRequest request,
                                             @RequestBody ChangeProfileRequestDto changeProfileRequestDto) {
        return profileService.changeIntroduce(GET_USER_ID_BY_TOKEN(request), changeProfileRequestDto);
    }

    @GetMapping("/")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> getUserInfo(HttpServletRequest request) {
        return profileService.getUserInfo(GET_USER_ID_BY_TOKEN(request));
    }
}
