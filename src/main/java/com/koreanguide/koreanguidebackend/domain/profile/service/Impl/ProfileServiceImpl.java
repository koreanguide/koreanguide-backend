package com.koreanguide.koreanguidebackend.domain.profile.service.Impl;

import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.auth.data.repository.UserRepository;
import com.koreanguide.koreanguidebackend.domain.credit.data.entity.BankAccounts;
import com.koreanguide.koreanguidebackend.domain.credit.data.entity.Credit;
import com.koreanguide.koreanguidebackend.domain.credit.data.enums.AccountProvider;
import com.koreanguide.koreanguidebackend.domain.credit.data.repository.BankAccountsRepository;
import com.koreanguide.koreanguidebackend.domain.credit.data.repository.CreditRepository;
import com.koreanguide.koreanguidebackend.domain.profile.data.dto.enums.Language;
import com.koreanguide.koreanguidebackend.domain.profile.data.dto.enums.SubwayLine;
import com.koreanguide.koreanguidebackend.domain.profile.data.dto.request.ChangeNearSubwayRequestDto;
import com.koreanguide.koreanguidebackend.domain.profile.data.dto.request.ChangePasswordRequestDto;
import com.koreanguide.koreanguidebackend.domain.profile.data.dto.request.ChangeProfileNonPasswordRequestDto;
import com.koreanguide.koreanguidebackend.domain.profile.data.dto.request.ChangeProfileRequestDto;
import com.koreanguide.koreanguidebackend.domain.profile.data.dto.response.MainInfoResponseDto;
import com.koreanguide.koreanguidebackend.domain.profile.data.dto.response.MyPageInfoResponseDto;
import com.koreanguide.koreanguidebackend.domain.profile.data.dto.response.MyPageResponseDto;
import com.koreanguide.koreanguidebackend.domain.profile.data.dto.response.ProfileResponseDto;
import com.koreanguide.koreanguidebackend.domain.profile.data.entity.Profile;
import com.koreanguide.koreanguidebackend.domain.profile.repository.ProfileRepository;
import com.koreanguide.koreanguidebackend.domain.profile.service.ProfileService;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.Track;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.TrackLike;
import com.koreanguide.koreanguidebackend.domain.track.data.repository.TrackLikeRepository;
import com.koreanguide.koreanguidebackend.domain.track.data.repository.TrackRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final BankAccountsRepository bankAccountsRepository;
    private final PasswordEncoder passwordEncoder;
    private final TrackLikeRepository trackLikeRepository;
    private final TrackRepository trackRepository;
    private final CreditRepository creditRepository;

    @Autowired
    public ProfileServiceImpl(ProfileRepository profileRepository, UserRepository userRepository,
                              BankAccountsRepository bankAccountsRepository, PasswordEncoder passwordEncoder,
                              TrackLikeRepository trackLikeRepository, TrackRepository trackRepository,
                              CreditRepository creditRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.bankAccountsRepository = bankAccountsRepository;
        this.passwordEncoder = passwordEncoder;
        this.trackLikeRepository = trackLikeRepository;
        this.trackRepository = trackRepository;
        this.creditRepository = creditRepository;
    }

    public Profile GET_PROFILE_BY_USER_ID(Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if(user.isEmpty()) {
            throw new RuntimeException("사용자 부정확");
        }

        Optional<Profile> profile = profileRepository.findByUser(user.get());

        return profile.orElseGet(() -> profileRepository.save(Profile.builder()
                .profileUrl("DEFAULT")
                .isPublic(true)
                .introduce(null)
                .phoneNum(null)
                .firstLang(Language.KOREAN)
                .secondLang(Language.ENGLISH)
                .subwayLine(null)
                .subwayStation(null)
                .birth(null)
                .name(null)
                .user(user.get())
                .build()));
    }

    public boolean CHECK_PASSWD(User user, String password) {
        return passwordEncoder.matches(password, user.getPassword());
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
        Profile profile = GET_PROFILE_BY_USER_ID(userId);

        profileResponseDto.setProfileUrl(profile.getProfileUrl());
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
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()) {
            throw new RuntimeException("미등록 사용자");
        }

//        User 정보
        myPageResponseDto.setNickName(user.get().getNickname());
        myPageResponseDto.setEmail(user.get().getEmail());
        myPageResponseDto.setEnable(user.get().isEnabled());
        myPageResponseDto.setPassword("********");
        myPageResponseDto.setBlocked("미등록");

//        Profile 정보
        Profile profile = GET_PROFILE_BY_USER_ID(userId);

        myPageResponseDto.setProfileUrl(profile.getProfileUrl());
        myPageResponseDto.setName(profile.getName() == null ? "미등록" : profile.getName());
        myPageResponseDto.setPhoneNum(profile.getPhoneNum() == null ? "미등록" : profile.getPhoneNum());
        myPageResponseDto.setIntroduce(profile.getIntroduce() == null ? "등록된 소개 글이 없습니다." : profile.getIntroduce());

        Optional<BankAccounts> bankAccounts = bankAccountsRepository.findBankAccountsByUser(user.get());
        if(bankAccounts.isEmpty()) {
            myPageResponseDto.setAccountInfo("미등록");
        } else {
            AccountProvider accountProvider = bankAccounts.get().getAccountProvider();
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

            myPageResponseDto.setAccountInfo(ACCOUNT_PROVIDER_KO_NAME + " " + bankAccounts.get().getAccountNumber());
        }

        return ResponseEntity.status(HttpStatus.OK).body(myPageResponseDto);
    }

    @Override
    public ResponseEntity<?> changeName(Long userId, ChangeProfileRequestDto changeProfileRequestDto) {
        Profile profile = GET_PROFILE_BY_USER_ID(userId);

        if(!CHECK_PASSWD(profile.getUser(), changeProfileRequestDto.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if(changeProfileRequestDto.getTarget().length() > 4) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("글자 수 제한(한글 4자)");
        }

        profile.setName(changeProfileRequestDto.getTarget());
        profileRepository.save(profile);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<?> changePhoneNum(Long userId, ChangeProfileRequestDto changeProfileRequestDto) {
        Profile profile = GET_PROFILE_BY_USER_ID(userId);

        if(!CHECK_PASSWD(profile.getUser(), changeProfileRequestDto.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if(!changeProfileRequestDto.getTarget().matches("^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("전화번호 형식이 올바르지 않습니다. 올바른 형식: 010-0000-0000");
        }

        profile.setPhoneNum(changeProfileRequestDto.getTarget());
        profileRepository.save(profile);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<?> removeProfileUrl(Long userId) {
        Profile profile = GET_PROFILE_BY_USER_ID(userId);

        profile.setProfileUrl("DEFAULT");
        profileRepository.save(profile);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<?> changeProfileUrl(Long userId, ChangeProfileRequestDto changeProfileRequestDto) {
        Profile profile = GET_PROFILE_BY_USER_ID(userId);
        String newProfileUrl = changeProfileRequestDto.getTarget();

        // 입력값 검증
        if(!"DEFAULT".equals(newProfileUrl) &&
                !newProfileUrl.matches("^https://koreanguide\\.s3\\.ap-northeast-2\\.amazonaws\\.com/.*$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("프로필 URL이 올바르지 않습니다. " +
                    "올바른 형식: https://koreanguide.s3.ap-northeast-2.amazonaws.com/<파일명>");
        }

        profile.setProfileUrl(newProfileUrl);
        profileRepository.save(profile);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<?> changePassword(Long userId, ChangePasswordRequestDto changePasswordRequestDto) {
        Optional<User> user = userRepository.findById(userId);

        if(user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if(!passwordEncoder.matches(changePasswordRequestDto.getPassword(), user.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.LOCKED).build();
        }

        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        if(!changePasswordRequestDto.getNewPassword().matches(passwordPattern)) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }

        User updatedUser = user.get();

        updatedUser.setPassword(passwordEncoder.encode(changePasswordRequestDto.getNewPassword()));
        userRepository.save(updatedUser);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<?> changeIntroduce(Long userId,
                                             ChangeProfileNonPasswordRequestDto changeProfileNonPasswordRequestDto) {
        Profile profile = GET_PROFILE_BY_USER_ID(userId);

        if(changeProfileNonPasswordRequestDto.getTarget().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        profile.setIntroduce(changeProfileNonPasswordRequestDto.getTarget());
        profileRepository.save(profile);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<?> changeNickname(Long userId, ChangeProfileRequestDto changeProfileRequestDto) {
        Optional<User> user = userRepository.findById(userId);

        if(user.isEmpty()) {
            throw new RuntimeException("USER NOT FOUND");
        }

        if(changeProfileRequestDto.getTarget().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if(!CHECK_PASSWD(user.get(), changeProfileRequestDto.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User updatedUser = user.get();
        updatedUser.setNickname(changeProfileRequestDto.getTarget());

        userRepository.save(updatedUser);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<?> getMainPageInfo(Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if(user.isEmpty()) {
            throw new RuntimeException("미등록 사용자");
        }

        Long totalLiked = 0L;
        Long newLikes = 0L;
        Long totalView = 0L;
        Long credit = 0L;

        List<Track> trackList = trackRepository.getAllByUser(user.get());
        for(Track track : trackList) {
            totalView += track.getViewCount();

            List<TrackLike> trackLikeList = trackLikeRepository.findAllByTrack(track);
            totalLiked += trackLikeList.size();

            if(user.get().getLastAccessTime() != null) {
                for(TrackLike like : trackLikeList) {
                    if(like.getLikedDt().isAfter(user.get().getLastAccessTime())) {
                        newLikes++;
                    }
                }
            }
        }

        Optional<Credit> creditInfo = creditRepository.findByUser(user.get());

        if(creditInfo.isPresent()) {
            credit = creditInfo.get().getAmount();
        }

        boolean isIncreased = newLikes > 0;

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
        Profile profile = GET_PROFILE_BY_USER_ID(userId);
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
        Profile profile = GET_PROFILE_BY_USER_ID(userId);

        if(changeNearSubwayRequestDto.getSubwayLine() == null || changeNearSubwayRequestDto.getStation().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        profile.setSubwayLine(changeNearSubwayRequestDto.getSubwayLine());
        profile.setSubwayStation(changeNearSubwayRequestDto.getStation());

        profileRepository.save(profile);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
