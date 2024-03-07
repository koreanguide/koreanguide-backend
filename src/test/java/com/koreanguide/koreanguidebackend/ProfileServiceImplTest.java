package com.koreanguide.koreanguidebackend;

import com.koreanguide.koreanguidebackend.domain.auth.data.dao.UserDao;
import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.auth.data.enums.SeoulCountry;
import com.koreanguide.koreanguidebackend.domain.profile.data.dao.ProfileDao;
import com.koreanguide.koreanguidebackend.domain.profile.data.dto.enums.SubwayLine;
import com.koreanguide.koreanguidebackend.domain.profile.data.dto.request.*;
import com.koreanguide.koreanguidebackend.domain.profile.data.entity.Profile;
import com.koreanguide.koreanguidebackend.domain.profile.service.Impl.ProfileServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ProfileServiceImplTest {
    @InjectMocks
    private ProfileServiceImpl profileService;

    @Mock
    private UserDao userDao;

    @Mock
    private ProfileDao profileDao;

    @Test
    @DisplayName("사용자 실명 등록 및 변경")
    public void changeNameTest() {
        User user = new User();
        user.setId(1L);
        user.setPassword("password");

        Profile profile = new Profile();
        profile.setUser(user);
        profile.setName("oldName");

        ChangeProfileRequestDto changeProfileRequestDto = new ChangeProfileRequestDto();
        changeProfileRequestDto.setPassword("password");
        changeProfileRequestDto.setTarget("전윤환");

        when(userDao.getUserEntity(any(Long.class))).thenReturn(user);
        when(userDao.checkPassword(any(User.class), any(String.class))).thenReturn(true);
        when(profileDao.getUserProfile(any(User.class))).thenReturn(profile);

        ResponseEntity<?> result = profileService.changeName(1L, changeProfileRequestDto);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("전윤환", profile.getName());
    }

    @Test
    @DisplayName("사용자 휴대폰 번호 등록 및 변경")
    public void changePhoneNum() {
        User user = new User();
        user.setId(1L);
        user.setPassword("password");

        Profile profile = new Profile();
        profile.setUser(user);
        profile.setPhoneNum("010-1234-1234");

        ChangeProfileRequestDto changeProfileRequestDto = new ChangeProfileRequestDto();
        changeProfileRequestDto.setPassword("password");
        changeProfileRequestDto.setTarget("010-5678-5678");

        when(userDao.getUserEntity(any(Long.class))).thenReturn(user);
        when(userDao.checkPassword(any(User.class), any(String.class))).thenReturn(true);
        when(profileDao.getUserProfile(any(User.class))).thenReturn(profile);

        ResponseEntity<?> result = profileService.changePhoneNum(1L, changeProfileRequestDto);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("010-5678-5678", profile.getPhoneNum());
    }

    @Test
    @DisplayName("사용자 프로필 사진 URL 변경, URL 형식 미일치")
    public void changeProfileUrlTest1() {
        User user = new User();
        user.setId(1L);
        user.setProfileUrl("DEFAULT");

        ChangeProfileRequestDto changeProfileRequestDto = new ChangeProfileRequestDto();
        changeProfileRequestDto.setPassword(null);
        changeProfileRequestDto.setTarget("https://naver.com");

        when(userDao.getUserEntity(any(Long.class))).thenReturn(user);

        ResponseEntity<?> result = profileService.changeProfileUrl(1L, changeProfileRequestDto);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("DEFAULT", user.getProfileUrl());
    }

    @Test
    @DisplayName("사용자 프로필 사진 URL 변경, URL 형식 일치")
    public void changeProfileUrlTest2() {
        User user = new User();
        user.setId(1L);
        user.setProfileUrl("DEFAULT");

        ChangeProfileRequestDto changeProfileRequestDto = new ChangeProfileRequestDto();
        changeProfileRequestDto.setPassword(null);
        changeProfileRequestDto.setTarget("https://koreanguide.s3.ap-northeast-2.amazonaws.com/asdasdasd.png");

        when(userDao.getUserEntity(any(Long.class))).thenReturn(user);

        ResponseEntity<?> result = profileService.changeProfileUrl(1L, changeProfileRequestDto);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("https://koreanguide.s3.ap-northeast-2.amazonaws.com/asdasdasd.png", user.getProfileUrl());
    }

    @Test
    @DisplayName("사용자 프로필 사진 제거")
    public void removeProfileUrl() {
        User user = new User();
        user.setId(1L);
        user.setProfileUrl("https://koreanguide.s3.ap-northeast-2.amazonaws.com/asdasdasd.png");

        when(userDao.getUserEntity(any(Long.class))).thenReturn(user);

        ResponseEntity<?> result = profileService.removeProfileUrl(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("DEFAULT", user.getProfileUrl());
    }

    @Test
    @DisplayName("사용자 소개글 등록 및 변경, 빈 String")
    public void changeIntroduceWhenEmpty() {
        User user = new User();
        user.setId(1L);

        Profile profile = new Profile();
        profile.setIntroduce("Default Introduce");

        ChangeProfileNonPasswordRequestDto changeProfileNonPasswordRequestDto = new ChangeProfileNonPasswordRequestDto();
        changeProfileNonPasswordRequestDto.setTarget("");

        when(userDao.getUserEntity(any(Long.class))).thenReturn(user);
        when(profileDao.getUserProfile(any(User.class))).thenReturn(profile);

        ResponseEntity<?> result = profileService.changeIntroduce(1L, changeProfileNonPasswordRequestDto);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Default Introduce", profile.getIntroduce());
    }

    @Test
    @DisplayName("사용자 소개글 등록 및 변경")
    public void changeIntroduce() {
        User user = new User();
        user.setId(1L);

        Profile profile = new Profile();
        profile.setIntroduce("Default Introduce");

        ChangeProfileNonPasswordRequestDto changeProfileNonPasswordRequestDto = new ChangeProfileNonPasswordRequestDto();
        changeProfileNonPasswordRequestDto.setTarget("Changed Introduce");

        when(userDao.getUserEntity(any(Long.class))).thenReturn(user);
        when(profileDao.getUserProfile(any(User.class))).thenReturn(profile);

        ResponseEntity<?> result = profileService.changeIntroduce(1L, changeProfileNonPasswordRequestDto);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Changed Introduce", profile.getIntroduce());
    }

    @Test
    @DisplayName("사용자 근처 지하철 역 변경 및 등록")
    public void changeNearSubway() {
        User user = new User();
        user.setId(1L);

        Profile profile = new Profile();
        profile.setSubwayLine(null);
        profile.setSubwayStation(null);

        ChangeNearSubwayRequestDto changeNearSubwayRequestDto = new ChangeNearSubwayRequestDto();
        changeNearSubwayRequestDto.setSubwayLine(SubwayLine.LINE_1);
        changeNearSubwayRequestDto.setStation("서울역");

        when(userDao.getUserEntity(any(Long.class))).thenReturn(user);
        when(profileDao.getUserProfile(any(User.class))).thenReturn(profile);

        ResponseEntity<?> result = profileService.changeNearSubway(1L, changeNearSubwayRequestDto);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(SubwayLine.LINE_1, profile.getSubwayLine());
        assertEquals("서울역", profile.getSubwayStation());
    }

    @Test
    @DisplayName("사용자 행정 구역 변경 및 등록")
    public void changeAddress() {
        User user = new User();
        user.setId(1L);
        user.setCountry(SeoulCountry.GANGNAM);

        ChangeAddressRequestDto changeAddressRequestDto = new ChangeAddressRequestDto();
        changeAddressRequestDto.setSeoulCountry(SeoulCountry.DONGJAK);

        when(userDao.getUserEntity(any(Long.class))).thenReturn(user);

        ResponseEntity<?> result = profileService.changeAddress(1L, changeAddressRequestDto);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(SeoulCountry.DONGJAK, user.getCountry());
    }

    @Test
    @DisplayName("사용자 생년월일 변경, 정규식 미일치")
    public void changeBirthTest1() {
        User user = new User();
        user.setId(1L);

        Profile profile = new Profile();
        profile.setBirth(null);

        ChangeBrithReqeustDto changeBrithReqeustDto = new ChangeBrithReqeustDto();
        changeBrithReqeustDto.setBirth("2003-08-30");

        when(userDao.getUserEntity(any(Long.class))).thenReturn(user);
        when(profileDao.getUserProfile(any(User.class))).thenReturn(profile);

        ResponseEntity<?> result = profileService.changeBirth(1L, changeBrithReqeustDto);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals(null, profile.getBirth());
    }

    @Test
    @DisplayName("사용자 생년월일 변경, 정규식 일치")
    public void changeBirthTest2() {
        User user = new User();
        user.setId(1L);

        Profile profile = new Profile();
        profile.setBirth(null);

        ChangeBrithReqeustDto changeBrithReqeustDto = new ChangeBrithReqeustDto();
        changeBrithReqeustDto.setBirth("20030830");

        when(userDao.getUserEntity(any(Long.class))).thenReturn(user);
        when(profileDao.getUserProfile(any(User.class))).thenReturn(profile);

        ResponseEntity<?> result = profileService.changeBirth(1L, changeBrithReqeustDto);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("20030830", profile.getBirth());
    }
}
