package com.koreanguide.koreanguidebackend.domain.profile.service.Impl;

import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.auth.data.repository.UserRepository;
import com.koreanguide.koreanguidebackend.domain.credit.data.entity.BankAccounts;
import com.koreanguide.koreanguidebackend.domain.credit.data.enums.AccountProvider;
import com.koreanguide.koreanguidebackend.domain.credit.data.repository.BankAccountsRepository;
import com.koreanguide.koreanguidebackend.domain.profile.data.dto.request.ChangeProfileRequestDto;
import com.koreanguide.koreanguidebackend.domain.profile.data.dto.response.MyPageResponseDto;
import com.koreanguide.koreanguidebackend.domain.profile.data.entity.Profile;
import com.koreanguide.koreanguidebackend.domain.profile.repository.ProfileRepository;
import com.koreanguide.koreanguidebackend.domain.profile.service.ProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final BankAccountsRepository bankAccountsRepository;

    @Autowired
    public ProfileServiceImpl(ProfileRepository profileRepository, UserRepository userRepository,
                              BankAccountsRepository bankAccountsRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.bankAccountsRepository = bankAccountsRepository;
    }

    public Profile GET_PROFILE_BY_USER_ID(Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if(user.isEmpty()) {
            throw new RuntimeException("사용자 부정확");
        }

        Optional<Profile> profile = profileRepository.findByUser(user.get());

        return profile.orElseGet(() -> profileRepository.save(Profile.builder()
                .isPublic(true)
                .introduce(null)
                .phoneNum(null)
                .name(null)
                .user(user.get())
                .build()));
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

        myPageResponseDto.setName(profile.getName() == null ? "미등록" : profile.getName());
        myPageResponseDto.setPhoneNum(profile.getPhoneNum() == null ? "미등록" : profile.getPhoneNum());

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

        if(profile.getName().equals(changeProfileRequestDto.getTarget())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
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

        if(profile.getPhoneNum().equals(changeProfileRequestDto.getTarget())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        if(!changeProfileRequestDto.getTarget().matches("^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("전화번호 형식이 올바르지 않습니다. 올바른 형식: 010-0000-0000");
        }

        profile.setPhoneNum(changeProfileRequestDto.getTarget());
        profileRepository.save(profile);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
