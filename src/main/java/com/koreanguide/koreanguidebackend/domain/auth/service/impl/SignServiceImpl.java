package com.koreanguide.koreanguidebackend.domain.auth.service.impl;

import com.koreanguide.koreanguidebackend.config.security.JwtTokenProvider;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.request.SignUpRequestDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.response.BaseResponseDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.response.SignAlertResponseDto;
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
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.*;
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
    private SpringTemplateEngine springTemplateEngine;

    @Autowired
    public SignServiceImpl(
            UserRepository userRepository,
            CreditRepository creditRepository,
            JwtTokenProvider jwtTokenProvider,
            PasswordEncoder passwordEncoder,
            JavaMailSender mailSender,
            RedisTemplate<String, String> redisTemplate,
            SpringTemplateEngine springTemplateEngine
            ) {
        this.userRepository = userRepository;
        this.creditRepository = creditRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.redisTemplate = redisTemplate;
        this.springTemplateEngine = springTemplateEngine;
    }

    private String generateAccessToken(String email, List<String> roles) {
        return jwtTokenProvider.createAccessToken(String.valueOf(email), roles);
    }

    private String generateRefreshToken(String email) {
        return jwtTokenProvider.createRefreshToken(String.valueOf(email));
    }

    private String generateAuthKey() {
        Random random = new Random();
        return String.valueOf(random.nextInt(1000000));
    }

    @Override
    public ResponseEntity<?> sendVerifyMail(String to) throws MessagingException {
        Long expireTime = redisTemplate.getExpire(to + ":resendTime", TimeUnit.SECONDS);
        if(expireTime > 0) {
            long min = expireTime / 60;
            long sec = expireTime % 60;
            String timeLeft = String.format("%02d:%02d", min, sec);
            return ResponseEntity.status(HttpStatus.LOCKED).body(SignAlertResponseDto.builder()
                            .en("You can resend the email authentication after " + timeLeft + ".")
                            .ko(timeLeft + " 후에 이메일 인증 재발송이 가능합니다.")
                    .build());
        }

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject("KOREAN GUIDE 이메일 인증 안내");

        String authKey = generateAuthKey();
        redisTemplate.opsForValue().set(to, authKey);
        redisTemplate.expire(to, 30, TimeUnit.MINUTES);

        redisTemplate.opsForValue().set(to + ":resendTime", authKey);
        redisTemplate.expire(to + ":resendTime", 1, TimeUnit.MINUTES);

        Context context = new Context();
        context.setVariable("key", authKey);

        String html = springTemplateEngine.process("verifyEmail.html", context);
        mimeMessageHelper.setText(html, true);

        mailSender.send(mimeMessage);

        return ResponseEntity.status(HttpStatus.OK).body(SignAlertResponseDto.builder()
                        .en("A membership authentication email has been sent normally.")
                        .ko("회원가입 인증 이메일이 정상적으로 발송되었습니다.")
                .build());
    }

    @Override
    public boolean validateAuthKey(String email, String inputKey) {
        String authKey = redisTemplate.opsForValue().get(email);
        return inputKey.equals(authKey);
    }

    @Override
    public ResponseEntity<?> signUp(SignUpRequestDto signUpRequestDto) {
        Optional<User> foundUserByEmail = userRepository.findByEmail(signUpRequestDto.getEmail());
        Optional<User> foundUserByNickname = userRepository.findByNickname(signUpRequestDto.getNickname());

        if (foundUserByEmail.isPresent()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    SignAlertResponseDto.builder()
                            .ko("이미 서비스에 등록된 이메일 주소입니다. 다른 이메일 주소를 사용하거나 로그인을 시도하십시오.")
                            .en("This email address is already registered with the service. " +
                                    "Please use a different email address or try to sign-in.")
                            .build()
                    );
        }

        if(foundUserByNickname.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    SignAlertResponseDto.builder()
                            .ko("사용할 수 없는 닉네임입니다. 다른 닉네임을 선택하십시오.")
                            .en("This is a nickname that cannot be used. Please select a different nickname.")
                            .build()
            );
        }

        if(!validateAuthKey(signUpRequestDto.getEmail(), signUpRequestDto.getAuthKey())) {
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(
                    SignAlertResponseDto.builder()
                            .ko("이메일 인증 유효 시간이 만료되었습니다. 이메일 인증을 다시 시도하십시오.")
                            .en("Email authentication has expired. Please try email authentication again.")
                            .build()
                    );
        }

        String emailPattern = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";

        if (!signUpRequestDto.getEmail().matches(emailPattern)) {
            throw new RuntimeException("이메일 형식 입력 오류");
        }

        if (!signUpRequestDto.getPassword().matches(passwordPattern)) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    SignAlertResponseDto.builder()
                            .ko("비밀번호는 8자리 이상이며, 특수문자, 대문자, 숫자를 각각 최소 1개 이상 포함해야 합니다.")
                            .en("Password cannot be used. Configure your password with at least 8 English " +
                                    "characters, at least 1 special character, at least 1 uppercase character, " +
                                    "and at least 1 character number.")
                            .build()
                    );
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

        return ResponseEntity.status(HttpStatus.OK).body(SignInResponseDto.builder()
                .isGuide(user.getUserRole().equals(UserRole.GUIDE))
                .accessToken(generateAccessToken(user.getEmail(), user.getRoles()))
                .refreshToken(generateRefreshToken(user.getEmail()))
                .email(user.getEmail())
                .msg("로그인 성공")
                .success(true)
                .build());
    }

    @Override
    public ResponseEntity<?> signIn(SignInRequestDto signInRequestDto) {
        Optional<User> user = userRepository.findByEmail(signInRequestDto.getEmail());

        if(user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("등록되지 않은 회원입니다.");
        }

        if (!passwordEncoder.matches(signInRequestDto.getPassword(), user.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        return ResponseEntity.status(HttpStatus.OK).body(SignInResponseDto.builder()
                        .isGuide(user.get().getUserRole().equals(UserRole.GUIDE))
                        .accessToken(generateAccessToken(user.get().getEmail(), user.get().getRoles()))
                        .refreshToken(generateRefreshToken(user.get().getEmail()))
                        .email(user.get().getEmail())
                        .msg("로그인 성공")
                        .success(true)
                .build());
    }
}
