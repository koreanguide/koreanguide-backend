package com.koreanguide.koreanguidebackend.domain.auth.service.impl;

import com.koreanguide.koreanguidebackend.config.security.JwtTokenProvider;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.request.SignUpRequestDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.response.BaseResponseDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.response.SignInResponseDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.request.SignInRequestDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.auth.data.enums.KoreaState;
import com.koreanguide.koreanguidebackend.domain.auth.data.enums.UserRole;
import com.koreanguide.koreanguidebackend.domain.auth.data.enums.UserType;
import com.koreanguide.koreanguidebackend.domain.auth.data.repository.UserRepository;
import com.koreanguide.koreanguidebackend.domain.auth.service.SignService;
import com.koreanguide.koreanguidebackend.domain.credit.data.entity.Credit;
import com.koreanguide.koreanguidebackend.domain.credit.data.repository.CreditRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SignServiceImpl implements SignService {
    @Value("${spring.mail.username}")
    private String from;

    private final UserRepository userRepository;
    private final CreditRepository creditRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public SignServiceImpl(
            UserRepository userRepository,
            CreditRepository creditRepository,
            JwtTokenProvider jwtTokenProvider,
            PasswordEncoder passwordEncoder,
            JavaMailSender mailSender,
            RedisTemplate<String, String> redisTemplate
            ) {
        this.userRepository = userRepository;
        this.creditRepository = creditRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.redisTemplate = redisTemplate;
    }

    private SignInResponseDto generateAuthToken(UserRole userRole, String email, List<String> roles) {
        return SignInResponseDto.builder()
                .accessToken(jwtTokenProvider.createAccessToken(String.valueOf(email), roles))
                .refreshToken(jwtTokenProvider.createRefreshToken(String.valueOf(email)))
                .email(email)
                .success(true)
                .msg("정상 로그인")
                .isGuide(userRole.equals(UserRole.GUIDE))
                .build();
    }

    private String generateAuthKey() {
        Random random = new Random();
        return String.valueOf(random.nextInt(1000000));
    }

    @Override
    public void sendVerifyMail(String to) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("회원가입 인증 메일입니다.");

        String authKey = generateAuthKey();
        redisTemplate.opsForValue().set(to, authKey);
        redisTemplate.expire(to, 30, TimeUnit.MINUTES);

        message.setText("인증번호는 " + authKey + "입니다.");

        mailSender.send(message);
    }

    @Override
    public boolean validateAuthKey(String email, String inputKey) {
        String authKey = redisTemplate.opsForValue().get(email);
        return inputKey.equals(authKey);
    }

    @Override
    public ResponseEntity<?> signUp(SignUpRequestDto signUpRequestDto) {
        if (userRepository.findByEmail(signUpRequestDto.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.LOCKED).body("사용 중인 이메일 주소를 입력했습니다.");
        }

        User user = User.builder()
                .email(signUpRequestDto.getEmail())
                .nickname(signUpRequestDto.getNickname())
                .userRole(signUpRequestDto.getUserRole())
                .state(KoreaState.SEOUL)
                .country(signUpRequestDto.getCountry())
                .password(passwordEncoder.encode(signUpRequestDto.getPassword()))
                .roles(Collections.singletonList("ROLE_USER"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        creditRepository.save(Credit.builder()
                        .recentUsed(LocalDateTime.now())
                        .amount(0L)
                        .user(user)
                .build());

        return ResponseEntity.status(HttpStatus.OK).body(
                generateAuthToken(user.getUserRole(), user.getEmail(), user.getRoles()));
    }

    @Override
    public SignInResponseDto signIn(SignInRequestDto signInRequestDto) {
        log.info("SignServiceImpl - signIn: 회원 조회 중");
        User user = userRepository.getByEmail(signInRequestDto.getEmail());

        if(user == null) {
            log.error("SignServiceImpl - signIn: 회원 조회 실패");
            throw new RuntimeException("사용자 정보를 찾을 수 없습니다.");
        }

        log.info("SignServiceImpl - signIn: email : {}", signInRequestDto.getEmail());

        log.info("SignServiceImpl - signIn: 패스워드 비교 시작");

        if (!passwordEncoder.matches(signInRequestDto.getPassword(), user.getPassword())) {
            log.error("SignServiceImpl - signIn: 비밀번호 불일치");
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        log.info("SignServiceImpl - signIn: 패스워드 일치");

        log.info("SignServiceImpl - signIn: 토큰 발급 및 기본 정보 반환 처리");

        return generateAuthToken(user.getUserRole(), user.getEmail(), user.getRoles());
    }
}
