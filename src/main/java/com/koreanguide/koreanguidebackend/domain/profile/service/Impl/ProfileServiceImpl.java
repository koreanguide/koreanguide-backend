package com.koreanguide.koreanguidebackend.domain.profile.service.Impl;

import com.koreanguide.koreanguidebackend.domain.auth.data.dao.UserDao;
import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.credit.data.dao.CreditDao;
import com.koreanguide.koreanguidebackend.domain.credit.data.entity.BankAccounts;
import com.koreanguide.koreanguidebackend.domain.credit.data.entity.Credit;
import com.koreanguide.koreanguidebackend.domain.credit.data.enums.AccountProvider;
import com.koreanguide.koreanguidebackend.domain.credit.exception.BankAccountsNotFoundException;
import com.koreanguide.koreanguidebackend.domain.profile.data.dao.ProfileDao;
import com.koreanguide.koreanguidebackend.domain.profile.data.dto.enums.Language;
import com.koreanguide.koreanguidebackend.domain.profile.data.dto.enums.SubwayLine;
import com.koreanguide.koreanguidebackend.domain.profile.data.dto.request.*;
import com.koreanguide.koreanguidebackend.domain.profile.data.dto.response.*;
import com.koreanguide.koreanguidebackend.domain.profile.data.entity.Profile;
import com.koreanguide.koreanguidebackend.domain.profile.service.ProfileService;
import com.koreanguide.koreanguidebackend.domain.track.data.dao.TrackDao;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.Track;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class ProfileServiceImpl implements ProfileService {
    private final UserDao userDao;
    private final TrackDao trackDao;
    private final ProfileDao profileDao;
    private final CreditDao creditDao;

    @Autowired
    public ProfileServiceImpl(ProfileDao profileDao, CreditDao creditDao,
                              UserDao userDao, TrackDao trackDao) {
        this.profileDao = profileDao;
        this.creditDao = creditDao;
        this.userDao = userDao;
        this.trackDao = trackDao;
    }

    public String TRANSLATE_LINE_TO_KO(SubwayLine subwayLine) {
        String SUBWAY_LINE_KO;
        if(subwayLine.equals(SubwayLine.LINE_1)) {
            SUBWAY_LINE_KO = "1호선";
        } else if (subwayLine.equals(SubwayLine.LINE_2)) {
            SUBWAY_LINE_KO = "2호선";
        } else if (subwayLine.equals(SubwayLine.LINE_3)) {
            SUBWAY_LINE_KO = "3호선";
        } else if (subwayLine.equals(SubwayLine.LINE_4)) {
            SUBWAY_LINE_KO = "4호선";
        } else if (subwayLine.equals(SubwayLine.LINE_5)) {
            SUBWAY_LINE_KO = "5호선";
        } else if (subwayLine.equals(SubwayLine.LINE_6)) {
            SUBWAY_LINE_KO = "6호선";
        } else if (subwayLine.equals(SubwayLine.LINE_7)) {
            SUBWAY_LINE_KO = "7호선";
        } else if (subwayLine.equals(SubwayLine.LINE_8)) {
            SUBWAY_LINE_KO = "8호선";
        } else if (subwayLine.equals(SubwayLine.LINE_9)) {
            SUBWAY_LINE_KO = "9호선";
        } else if (subwayLine.equals(SubwayLine.SUINBUNDANG)) {
            SUBWAY_LINE_KO = "수인분당선";
        } else if (subwayLine.equals(SubwayLine.SHINBUNDANG)) {
            SUBWAY_LINE_KO = "신분당선";
        } else if (subwayLine.equals(SubwayLine.GYEONGUIJUNGANG)) {
            SUBWAY_LINE_KO = "경의중앙선";
        } else if (subwayLine.equals(SubwayLine.GIMPOGOLD)) {
            SUBWAY_LINE_KO = "김포골드라인";
        } else if (subwayLine.equals(SubwayLine.AIRPORTRAILROAD)) {
            SUBWAY_LINE_KO = "공항철도";
        } else if (subwayLine.equals(SubwayLine.SEOHAE)) {
            SUBWAY_LINE_KO = "서해선";
        } else if (subwayLine.equals(SubwayLine.SINLIM)) {
            SUBWAY_LINE_KO = "신림선";
        } else {
            SUBWAY_LINE_KO = "?";
        }

        return SUBWAY_LINE_KO;
    }

    @Override
    public ResponseEntity<?> getUserProfile(Long userId) {
        ProfileResponseDto profileResponseDto = new ProfileResponseDto();
        Profile profile = profileDao.getUserProfile(userId);

        profileResponseDto.setProfileUrl(profile.getUser().getProfileUrl());
        profileResponseDto.setNickName(profile.getUser().getNickname());
        profileResponseDto.setIntroduce(profile.getIntroduce());
        profileResponseDto.setFirstLang(profile.getFirstLang().equals(Language.KOREAN) ? "한국어" : "영어");
        profileResponseDto.setSecondLang(profile.getSecondLang().equals(Language.ENGLISH) ? "영어" : "한국어");
        profileResponseDto.setNearSubway(TRANSLATE_LINE_TO_KO(profile.getSubwayLine()) + " " + profile.getSubwayStation());

        String birthFormat = "yyyy년 M월 dd일";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        try {
            Date birth = sdf.parse(profile.getBirth());
            sdf.applyPattern(birthFormat);
            String newBirthFormat = sdf.format(birth);
            profileResponseDto.setBirth(newBirthFormat);
        } catch (ParseException e) {
            profileResponseDto.setBirth("표시할 수 없음");
        }

        profileResponseDto.setSubwayLine(profile.getSubwayLine());
        profileResponseDto.setAddress(profile.getUser().getCountry());

        return ResponseEntity.status(HttpStatus.OK).body(profileResponseDto);
    }

    @Override
    public ResponseEntity<?> getUserInfo(Long userId) {
        MyPageResponseDto myPageResponseDto = new MyPageResponseDto();

//        사용자 정보 확인
        User user = userDao.getUserEntity(userId);
        Profile profile = profileDao.getUserProfile(userId);

        myPageResponseDto.setNickName(user.getNickname());
        myPageResponseDto.setEmail(user.getEmail());
        myPageResponseDto.setEnable(user.isEnabled());
        myPageResponseDto.setPassword("********");
        myPageResponseDto.setBlocked("미등록");
        myPageResponseDto.setProfileUrl(profile.getUser().getProfileUrl());
        myPageResponseDto.setName(profile.getName() == null ? "미등록" : profile.getName());
        myPageResponseDto.setPhoneNum(profile.getPhoneNum() == null ? "미등록" : profile.getPhoneNum());
        myPageResponseDto.setIntroduce(profile.getIntroduce() == null ? "등록된 소개 글이 없습니다." : profile.getIntroduce());

        try {
            BankAccounts bankAccounts = creditDao.getBankAccountsEntityViaUser(user);
            AccountProvider accountProvider = bankAccounts.getAccountProvider();
            String ACCOUNT_PROVIDER_KO_NAME = "";

            if(accountProvider.equals(AccountProvider.KYONGNAMBANK)) {
                ACCOUNT_PROVIDER_KO_NAME = "경남";
            } else if (accountProvider.equals(AccountProvider.GWANGJUBANK)) {
                ACCOUNT_PROVIDER_KO_NAME = "광주";
            } else if (accountProvider.equals(AccountProvider.LOCALNONGHYEOP)) {
                ACCOUNT_PROVIDER_KO_NAME = "축농협";
            } else if (accountProvider.equals(AccountProvider.BUSANBANK)) {
                ACCOUNT_PROVIDER_KO_NAME = "부산";
            } else if (accountProvider.equals(AccountProvider.SAEMAUL)) {
                ACCOUNT_PROVIDER_KO_NAME = "새마을";
            } else if (accountProvider.equals(AccountProvider.SANLIM)) {
                ACCOUNT_PROVIDER_KO_NAME = "산림";
            } else if (accountProvider.equals(AccountProvider.SHINHYEOP)) {
                ACCOUNT_PROVIDER_KO_NAME = "신협";
            } else if (accountProvider.equals(AccountProvider.CITI)) {
                ACCOUNT_PROVIDER_KO_NAME = "시티";
            } else if (accountProvider.equals(AccountProvider.WOORI)) {
                ACCOUNT_PROVIDER_KO_NAME = "우리";
            } else if (accountProvider.equals(AccountProvider.POST)) {
                ACCOUNT_PROVIDER_KO_NAME = "우체국";
            } else if (accountProvider.equals(AccountProvider.SAVINGBANK)) {
                ACCOUNT_PROVIDER_KO_NAME = "저축";
            } else if (accountProvider.equals(AccountProvider.JEONBUKBANK)) {
                ACCOUNT_PROVIDER_KO_NAME = "전북";
            } else if (accountProvider.equals(AccountProvider.JEJUBANK)) {
                ACCOUNT_PROVIDER_KO_NAME = "제주";
            } else if (accountProvider.equals(AccountProvider.KAKAOBANK)) {
                ACCOUNT_PROVIDER_KO_NAME = "카카오";
            } else if (accountProvider.equals(AccountProvider.TOSSBANK)) {
                ACCOUNT_PROVIDER_KO_NAME = "토스";
            } else if (accountProvider.equals(AccountProvider.HANA)) {
                ACCOUNT_PROVIDER_KO_NAME = "하나";
            } else if (accountProvider.equals(AccountProvider.HSBC)) {
                ACCOUNT_PROVIDER_KO_NAME = "홍콩상하이";
            } else if (accountProvider.equals(AccountProvider.IBK)) {
                ACCOUNT_PROVIDER_KO_NAME = "IBK";
            } else if (accountProvider.equals(AccountProvider.KOOKMIN)) {
                ACCOUNT_PROVIDER_KO_NAME = "국민";
            } else if (accountProvider.equals(AccountProvider.DAEGUBANK)) {
                ACCOUNT_PROVIDER_KO_NAME = "대구";
            } else if (accountProvider.equals(AccountProvider.KDBBANK)) {
                ACCOUNT_PROVIDER_KO_NAME = "산업";
            } else if (accountProvider.equals(AccountProvider.NONGHYEOP)) {
                ACCOUNT_PROVIDER_KO_NAME = "농협";
            } else if (accountProvider.equals(AccountProvider.SC)) {
                ACCOUNT_PROVIDER_KO_NAME = "SC";
            } else if (accountProvider.equals(AccountProvider.SUHYEOP)) {
                ACCOUNT_PROVIDER_KO_NAME = "수협";
            } else if (accountProvider.equals(AccountProvider.SHINHAN)) {
                ACCOUNT_PROVIDER_KO_NAME = "신한";
            }

            myPageResponseDto.setAccountInfo(ACCOUNT_PROVIDER_KO_NAME + " " + bankAccounts.getAccountNumber());
        } catch (BankAccountsNotFoundException e) {
            myPageResponseDto.setAccountInfo("미등록");
        }

        return ResponseEntity.status(HttpStatus.OK).body(myPageResponseDto);
    }

    @Override
    public ResponseEntity<?> changeName(Long userId, ChangeProfileRequestDto changeProfileRequestDto) {
        Profile profile = profileDao.getUserProfile(userId);

        if(!userDao.checkPassword(profile.getUser(), changeProfileRequestDto.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if(changeProfileRequestDto.getTarget().length() > 4) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("글자 수 제한(한글 4자)");
        }

        profile.setName(changeProfileRequestDto.getTarget());
        profileDao.saveProfileEntity(profile);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<?> changePhoneNum(Long userId, ChangeProfileRequestDto changeProfileRequestDto) {
        Profile profile = profileDao.getUserProfile(userId);

        if(!userDao.checkPassword(profile.getUser(), changeProfileRequestDto.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if(!changeProfileRequestDto.getTarget().matches("^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("전화번호 형식이 올바르지 않습니다. 올바른 형식: 010-0000-0000");
        }

        profile.setPhoneNum(changeProfileRequestDto.getTarget());
        profileDao.saveProfileEntity(profile);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<?> removeProfileUrl(Long userId) {
        User user = userDao.getUserEntity(userId);

        user.setProfileUrl("DEFAULT");
        userDao.saveUserEntity(user);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<?> changeProfileUrl(Long userId, ChangeProfileRequestDto changeProfileRequestDto) {
        User user = userDao.getUserEntity(userId);
        String newProfileUrl = changeProfileRequestDto.getTarget();

        // 입력값 검증
        if(!"DEFAULT".equals(newProfileUrl) &&
                !newProfileUrl.matches("^https://koreanguide\\.s3\\.ap-northeast-2\\.amazonaws\\.com/.*$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("프로필 URL이 올바르지 않습니다. " +
                    "올바른 형식: https://koreanguide.s3.ap-northeast-2.amazonaws.com/<파일명>");
        }

        user.setProfileUrl(newProfileUrl);
        userDao.saveUserEntity(user);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<?> changePassword(Long userId, ChangePasswordRequestDto changePasswordRequestDto) {
        User user = userDao.getUserEntity(userId);
        userDao.changePassword(user, changePasswordRequestDto.getPassword());

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<?> changeIntroduce(Long userId,
                                             ChangeProfileNonPasswordRequestDto changeProfileNonPasswordRequestDto) {
        Profile profile = profileDao.getUserProfile(userId);

        if(changeProfileNonPasswordRequestDto.getTarget().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        profile.setIntroduce(changeProfileNonPasswordRequestDto.getTarget());
        profileDao.saveProfileEntity(profile);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<?> changeNickname(Long userId, ChangeProfileRequestDto changeProfileRequestDto) {
        User user = userDao.getUserEntity(userId);

        if(changeProfileRequestDto.getTarget().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if(!userDao.checkPassword(user, changeProfileRequestDto.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        user.setNickname(changeProfileRequestDto.getTarget());
        userDao.saveUserEntity(user);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<?> getMainPageInfo(Long userId) {
        User user = userDao.getUserEntity(userId);

        Long totalLiked = 0L;
        long newLikes = 0L;
        Long totalView = 0L;
        Long credit = 0L;

        List<Track> trackList = trackDao.getUserAllTrack(user);
        for(Track track : trackList) {
            totalView += track.getViewCount();
            totalLiked += trackDao.trackLikeCount(track);
        }

        Credit creditInfo = creditDao.getUserCreditEntity(userId);
        credit = creditInfo.getAmount();

        boolean isIncreased = false;

        return ResponseEntity.status(HttpStatus.OK).body(MainInfoResponseDto.builder()
                .totalView(totalView)
                .totalLiked(totalLiked)
                .credit(credit)
                .isIncreased(isIncreased)
                .increasedAmount(newLikes)
                .build());
    }

    @Override
    public ResponseEntity<?> getMyPageInfo(Long userId) {
        Profile profile = profileDao.getUserProfile(userId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 a h시 m분");
        String formatDateTime = profile.getUser().getCreatedAt().format(formatter);
        return ResponseEntity.status(HttpStatus.OK).body(MyPageInfoResponseDto.builder()
                        .email(profile.getUser().getEmail())
                        .password("********")
                        .name(profile.getName())
                        .phoneNum(profile.getPhoneNum())
                        .registeredAt(formatDateTime)
                .build());
    }

    @Override
    public ResponseEntity<?> changeNearSubway(Long userId, ChangeNearSubwayRequestDto changeNearSubwayRequestDto) {
        Profile profile = profileDao.getUserProfile(userId);

        if(changeNearSubwayRequestDto.getSubwayLine() == null || changeNearSubwayRequestDto.getStation().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        profile.setSubwayLine(changeNearSubwayRequestDto.getSubwayLine());
        profile.setSubwayStation(changeNearSubwayRequestDto.getStation());

        profileDao.saveProfileEntity(profile);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<?> changeAddress(Long userId, ChangeAddressRequestDto changeAddressRequestDto) {
        if(changeAddressRequestDto.getSeoulCountry() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        User user = userDao.getUserEntity(userId);
        user.setCountry(changeAddressRequestDto.getSeoulCountry());

        userDao.saveUserEntity(user);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<?> changeBirth(Long userId, ChangeBrithReqeustDto changeBrithReqeustDto) {
        Profile profile = profileDao.getUserProfile(userId);

        String birth = changeBrithReqeustDto.getBirth();
        Pattern pattern = Pattern.compile("^\\d{8}$");
        Matcher matcher = pattern.matcher(birth);

        if (!matcher.matches()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("생년월일 정규식 미일치");
        }

        profile.setBirth(birth);
        profileDao.saveProfileEntity(profile);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<?> getInfoBoxInfo(Long userId) {
        Profile profile = profileDao.getUserProfile(userId);
        Credit credit = creditDao.getUserCreditEntity(userId);

        return ResponseEntity.status(HttpStatus.OK).body(InfoBoxResponseDto.builder()
                        .name(profile.getUser().getNickname())
                        .profileUrl(profile.getUser().getProfileUrl())
                        .email(profile.getUser().getEmail())
                        .credit(credit.getAmount())
                .build());
    }
}
