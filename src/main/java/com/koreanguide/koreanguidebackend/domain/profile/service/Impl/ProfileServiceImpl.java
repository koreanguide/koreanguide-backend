package com.koreanguide.koreanguidebackend.domain.profile.service.Impl;

import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.auth.data.repository.UserRepository;
import com.koreanguide.koreanguidebackend.domain.credit.data.entity.BankAccounts;
import com.koreanguide.koreanguidebackend.domain.credit.data.entity.Credit;
import com.koreanguide.koreanguidebackend.domain.credit.data.enums.AccountProvider;
import com.koreanguide.koreanguidebackend.domain.credit.data.repository.BankAccountsRepository;
import com.koreanguide.koreanguidebackend.domain.credit.data.repository.CreditRepository;
import com.koreanguide.koreanguidebackend.domain.profile.data.dto.request.ChangePasswordRequestDto;
import com.koreanguide.koreanguidebackend.domain.profile.data.dto.request.ChangeProfileRequestDto;
import com.koreanguide.koreanguidebackend.domain.profile.data.dto.response.MainInfoResponseDto;
import com.koreanguide.koreanguidebackend.domain.profile.data.dto.response.MyPageResponseDto;
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
                .name(null)
                .user(user.get())
                .build()));
    }

    public boolean CHECK_PASSWD(User user, String password) {
        return passwordEncoder.matches(password, user.getPassword());
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
    public ResponseEntity<?> changeIntroduce(Long userId, ChangeProfileRequestDto changeProfileRequestDto) {
        Profile profile = GET_PROFILE_BY_USER_ID(userId);

        if(changeProfileRequestDto.getTarget().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if(!CHECK_PASSWD(profile.getUser(), changeProfileRequestDto.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        profile.setIntroduce(changeProfileRequestDto.getTarget());
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
}
