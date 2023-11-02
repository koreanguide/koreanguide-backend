package com.koreanguide.koreanguidebackend.domain.auth.service.impl;

import com.koreanguide.koreanguidebackend.config.security.JwtTokenProvider;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.BaseResponseDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.SignInResponseDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.SignRequestDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.auth.data.repository.UserRepository;
import com.koreanguide.koreanguidebackend.domain.auth.service.SignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;

@Slf4j
@Service
public class SignServiceImpl implements SignService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SignServiceImpl(
            UserRepository userRepository,
            JwtTokenProvider jwtTokenProvider,
            PasswordEncoder passwordEncoder
            ) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public BaseResponseDto signUp(SignRequestDto signRequestDto) {
        if(signRequestDto.getEmail() == null || signRequestDto.getPassword() == null) {
            return BaseResponseDto.builder()
                    .success(false)
                    .msg("이메일 또는 비밀번호가 입력되지 않았습니다.")
                    .build();
        }

        if (userRepository.findByEmail(signRequestDto.getEmail()) != null) {
            return BaseResponseDto.builder()
                    .success(false)
                    .msg("이미 가입된 이메일입니다.")
                    .build();
        }

        User user = User.builder()
                .email(signRequestDto.getEmail())
                .password(passwordEncoder.encode(signRequestDto.getPassword()))
                .roles(Collections.singletonList("ROLE_USER"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        return BaseResponseDto.builder()
                .success(true)
                .msg("회원가입이 완료되었습니다.")
                .build();
    }

    @Override
    public SignInResponseDto signIn(SignRequestDto signRequestDto) {
        log.info("SignServiceImpl - signIn: 회원 조회 중");
        User user = userRepository.getByEmail(signRequestDto.getEmail());

        if(user == null) {
            log.info("SignServiceImpl - signIn: 회원 조회 실패");
            throw new RuntimeException("사용자 정보를 찾을 수 없습니다.");
        }

        log.info("SignServiceImpl - signIn: email : {}", signRequestDto.getEmail());

        log.info("SignServiceImpl - signIn: 패스워드 비교 시작");

        if (!passwordEncoder.matches(signRequestDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        log.info("SignServiceImpl - signIn: 패스워드 일치");

        log.info("SignServiceImpl - signIn: 토큰 발급 및 기본 정보 반환 준비 중");
        SignInResponseDto signInResponseDto = SignInResponseDto.builder()
                .accessToken(jwtTokenProvider.createAccessToken(String.valueOf(user.getEmail()), user.getRoles()))
                .refreshToken(jwtTokenProvider.createRefreshToken(String.valueOf(user.getEmail())))
                .email(user.getEmail())
                .success(true)
                .msg("로그인에 성공하였습니다.")
                .build();

        log.info("SignServiceImpl - signIn: 토큰 발급 및 기본 정보 반환 준비 완료");

        return signInResponseDto;
    }
}
