package kr.yuns.profile;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import kr.yuns.auth.data.dao.UserDao;
import kr.yuns.profile.data.dto.request.ChangeAddressRequestDto;
import kr.yuns.profile.data.dto.request.ChangeBrithReqeustDto;
import kr.yuns.profile.data.dto.request.ChangeNearSubwayRequestDto;
import kr.yuns.profile.data.dto.request.ChangePasswordRequestDto;
import kr.yuns.profile.data.dto.request.ChangeProfileNonPasswordRequestDto;
import kr.yuns.profile.data.dto.request.ChangeProfileRequestDto;
import kr.yuns.profile.service.ProfileService;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/profile")
public class ProfileController {
    private final ProfileService profileService;
    private final UserDao userDao;

    public Long GET_USER_ID_BY_TOKEN(HttpServletRequest request) {
        return userDao.getUserId(request.getHeader("X-AUTH-TOKEN"));
    }

    @PostMapping("/name")
    public ResponseEntity<?> changeName(HttpServletRequest request,
                                        @RequestBody ChangeProfileRequestDto changeProfileRequestDto) {
        return profileService.changeName(GET_USER_ID_BY_TOKEN(request), changeProfileRequestDto);
    }

    @PostMapping("/phone")
    public ResponseEntity<?> changePhoneNum(HttpServletRequest request,
                                            @RequestBody ChangeProfileRequestDto changeProfileRequestDto) {
        return profileService.changePhoneNum(GET_USER_ID_BY_TOKEN(request), changeProfileRequestDto);
    }

    @PostMapping("/profile")
    public ResponseEntity<?> changeProfileUrl(HttpServletRequest request,
                                              @RequestBody ChangeProfileRequestDto changeProfileRequestDto) {
        return profileService.changeProfileUrl(GET_USER_ID_BY_TOKEN(request), changeProfileRequestDto);
    }

    // @GetMapping("/progress")
    // public ResponseEntity<MainProfileAlertResponseDto> getMainPageProfileAlert(HttpServletRequest request) {
    //     return profileService.getMainPageProfileAlert(GET_USER_ID_BY_TOKEN(request));
    // }

    // @PostMapping("/progress/deposit")
    // public ResponseEntity<?> depositMainPageProfileCompleteCredit(HttpServletRequest request) {
    //     return profileService.depositMainPageProfileCompleteCredit(GET_USER_ID_BY_TOKEN(request));
    // }

    @DeleteMapping("/profile")
    public ResponseEntity<?> removeProfileUrl(HttpServletRequest request) {
        return profileService.removeProfileUrl(GET_USER_ID_BY_TOKEN(request));
    }

    @PostMapping("/introduce")
    public ResponseEntity<?> changeIntroduce(HttpServletRequest request,
                                             @RequestBody ChangeProfileNonPasswordRequestDto changeProfileNonPasswordRequestDto) {
        return profileService.changeIntroduce(GET_USER_ID_BY_TOKEN(request), changeProfileNonPasswordRequestDto);
    }

    @PostMapping("/password")
    public ResponseEntity<?> changePassword(HttpServletRequest request,
                                            @RequestBody ChangePasswordRequestDto changePasswordRequestDto) {
        return profileService.changePassword(GET_USER_ID_BY_TOKEN(request), changePasswordRequestDto);
    }

    @PostMapping("/nickname")
    public ResponseEntity<?> changeNickname(HttpServletRequest request,
                                            @RequestBody ChangeProfileRequestDto changeProfileRequestDto) {
        return profileService.changeNickname(GET_USER_ID_BY_TOKEN(request), changeProfileRequestDto);
    }

    // @GetMapping("/")
    // public ResponseEntity<?> getUserInfo(HttpServletRequest request) {
    //     return profileService.getUserInfo(GET_USER_ID_BY_TOKEN(request));
    // }

    @GetMapping("/info")
    public ResponseEntity<?> getUserProfile(HttpServletRequest request) {
        return profileService.getUserProfile(GET_USER_ID_BY_TOKEN(request));
    }

    // @GetMapping("/main")
    // public ResponseEntity<?> getMainPageInfo(HttpServletRequest request) {
    //     return profileService.getMainPageInfo(GET_USER_ID_BY_TOKEN(request));
    // }

    @GetMapping("/mypage")
    public ResponseEntity<?> getMyPageInfo(HttpServletRequest request) {
        return profileService.getMyPageInfo(GET_USER_ID_BY_TOKEN(request));
    }

    @PostMapping("/subway")
    public ResponseEntity<?> changeNearSubway(HttpServletRequest request,
                                              @RequestBody ChangeNearSubwayRequestDto changeNearSubwayRequestDto) {
        return profileService.changeNearSubway(GET_USER_ID_BY_TOKEN(request), changeNearSubwayRequestDto);
    }

    @PostMapping("/address")
    public ResponseEntity<?> changeAddress(HttpServletRequest request,
                                           @RequestBody ChangeAddressRequestDto changeAddressRequestDto) {
        return profileService.changeAddress(GET_USER_ID_BY_TOKEN(request), changeAddressRequestDto);
    }

    @PostMapping("/birth")
    public ResponseEntity<?> changeBirth(HttpServletRequest request,
                                         @RequestBody ChangeBrithReqeustDto changeBrithReqeustDto) {
        return profileService.changeBirth(GET_USER_ID_BY_TOKEN(request), changeBrithReqeustDto);
    }

    // @GetMapping("/infobox")
    // public ResponseEntity<?> getInfoBoxInfo(HttpServletRequest request) {
    //     return profileService.getInfoBoxInfo(GET_USER_ID_BY_TOKEN(request));
    // }
}