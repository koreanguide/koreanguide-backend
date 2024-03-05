package com.koreanguide.koreanguidebackend.domain.auth.service.impl;

import com.koreanguide.koreanguidebackend.config.security.JwtTokenProvider;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.request.ResetPasswordRequestDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.request.SignUpRequestDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.request.TokenRequestDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.response.SignAlertResponseDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.response.SignInResponseDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.request.SignInRequestDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.response.TokenResponseDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.auth.data.enums.KoreaState;
import com.koreanguide.koreanguidebackend.domain.auth.data.enums.UserRole;
import com.koreanguide.koreanguidebackend.domain.auth.data.enums.VerifyType;
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
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    private final UserDetailsService userDetailsService;

    @Autowired
    public SignServiceImpl(
            UserRepository userRepository,
            CreditRepository creditRepository,
            JwtTokenProvider jwtTokenProvider,
            PasswordEncoder passwordEncoder,
            JavaMailSender mailSender,
            RedisTemplate<String, String> redisTemplate,
            SpringTemplateEngine springTemplateEngine,
            UserDetailsService userDetailsService
            ) {
        this.userRepository = userRepository;
        this.creditRepository = creditRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.redisTemplate = redisTemplate;
        this.springTemplateEngine = springTemplateEngine;
        this.userDetailsService = userDetailsService;
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

    private boolean matchEmailPattern(String email) {
        String emailPattern = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        return email.matches(emailPattern);
    }

    @Override
    public ResponseEntity<?> sendVerifyMail(String to) throws MessagingException {
        Optional<User> user = userRepository.findByEmail(to);

        if(user.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(SignAlertResponseDto.builder()
                            .en("This email address is already registered with the service. " +
                                    "Please use a different email address or try to sign-in.")
                            .ko("이미 서비스에 등록된 이메일 주소입니다. 다른 이메일 주소를 사용하거나 로그인을 시도하십시오.")
                    .build());
        }

        if(!matchEmailPattern(to)) {
            throw new RuntimeException("이메일 형식 입력 오류");
        }
        
        Long expireTime = redisTemplate.getExpire(to + ":validateSignUpEmailResendTime", TimeUnit.SECONDS);
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
        redisTemplate.opsForValue().set(to + ":validateSignUpEmail", authKey);
        redisTemplate.expire(to + ":validateSignUpEmail", 30, TimeUnit.MINUTES);

        redisTemplate.opsForValue().set(to + ":validateSignUpEmailResendTime", authKey);
        redisTemplate.expire(to + ":validateSignUpEmailResendTime", 1, TimeUnit.MINUTES);

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
    public boolean validateAuthKey(VerifyType verifyType, String email, String inputKey) {
        String validateType;

        if(verifyType.equals(VerifyType.SIGNUP)) {
            validateType = ":validateSignUpEmail";
        } else {
            validateType = ":validateResetPasswordEmail";
        }

        String authKey = redisTemplate.opsForValue().get(email + validateType);
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

        if(!validateAuthKey(VerifyType.SIGNUP, signUpRequestDto.getEmail(), signUpRequestDto.getAuthKey())) {
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(
                    SignAlertResponseDto.builder()
                            .ko("이메일 인증 유효 시간이 만료되었습니다. 이메일 인증을 다시 시도하십시오.")
                            .en("Email authentication has expired. Please try email authentication again.")
                            .build()
                    );
        }

        if(!matchEmailPattern(signUpRequestDto.getEmail())) {
            throw new RuntimeException("이메일 형식 입력 오류");
        }

        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";

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
                .lastAccessTime(LocalDateTime.now())
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
                .name(user.getNickname())
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

        User updatedUser = user.get();
        updatedUser.setLastAccessTime(LocalDateTime.now());

        userRepository.save(updatedUser);

        String GENERATED_ACCESS_TOKEN = generateAccessToken(user.get().getEmail(), user.get().getRoles());
        String GENERATED_REFRESH_TOKEN = generateRefreshToken(user.get().getEmail());

        String key = "REFRESH_TOKEN:" + user.get().getEmail();
        redisTemplate.opsForValue().set(key, GENERATED_REFRESH_TOKEN, 1209600, TimeUnit.SECONDS);

        return ResponseEntity.status(HttpStatus.OK).body(SignInResponseDto.builder()
                        .isGuide(user.get().getUserRole().equals(UserRole.GUIDE))
                        .accessToken(GENERATED_ACCESS_TOKEN)
                        .refreshToken(GENERATED_REFRESH_TOKEN)
                        .name(user.get().getNickname())
                        .email(user.get().getEmail())
                .build());
    }

    @Override
    public ResponseEntity<?> resetPassword(ResetPasswordRequestDto resetPasswordRequestDto) {
        if(!matchEmailPattern(resetPasswordRequestDto.getEmail())) {
            throw new RuntimeException("이메일 형식 입력 오류");
        }

        if(!validateAuthKey(VerifyType.RESET_PASSWORD, resetPasswordRequestDto.getEmail(),
                resetPasswordRequestDto.getValidateKey())) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";

        if(!passwordPattern.matches(passwordPattern)) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(SignAlertResponseDto.builder()
                            .en("Password cannot be used. Configure your password with at least 8 " +
                                    "English characters, at least 1 special character, at least 1 " +
                                    "uppercase character, and at least 1 character number.")
                            .ko("사용할 수 없는 비밀번호입니다. 영문 8자리 이상, 1개 이상의 특수문자, 1자 이상의 대문자, " +
                                    "1자 이상의 숫자를 이용하여 비밀번호를 구성하십시오.")
                    .build());
        }

        Optional<User> user = userRepository.findByEmail(resetPasswordRequestDto.getEmail());

        if(user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(SignAlertResponseDto.builder()
                            .ko("입력한 이메일 주소로 등록된 회원을 찾을 수 없습니다. 이메일 주소를 다시 한 번 확인하십시오.")
                            .en("No registered members could be found with the email address you entered. " +
                                    "Please double check your email address.")
                    .build());
        }

        if(passwordEncoder.matches(resetPasswordRequestDto.getPassword(), user.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.LOCKED).body(SignAlertResponseDto.builder()
                            .en("The new password is not different from the current password.")
                            .ko("새로운 비밀번호가 현재 비밀번호와 다르지 않습니다.")
                    .build());
        }

        User updatedUser = user.get();
        updatedUser.setPassword(passwordEncoder.encode(resetPasswordRequestDto.getPassword()));

        userRepository.save(updatedUser);

        return ResponseEntity.status(HttpStatus.OK).body(SignAlertResponseDto.builder()
                        .ko("비밀번호 변경이 정상적으로 완료되었습니다.")
                        .en("The password change has been completed successfully.")
                .build());
    }

    @Override
    public ResponseEntity<?> sendResetPasswordVerifyMail(String to) throws MessagingException {
        if(!matchEmailPattern(to)) {
            throw new RuntimeException("이메일 형식 입력 오류");
        }

        Long expireTime = redisTemplate.getExpire(to + ":validateResetPasswordEmailResendTime", TimeUnit.SECONDS);
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
        mimeMessageHelper.setSubject("KOREAN GUIDE 비밀번호 재설정 이메일 인증");

        String authKey = generateAuthKey();
        redisTemplate.opsForValue().set(to + ":validateResetPasswordEmail", authKey);
        redisTemplate.expire(to + ":validateResetPasswordEmail", 30, TimeUnit.MINUTES);

        redisTemplate.opsForValue().set(to + ":validateResetPasswordEmailResendTime", authKey);
        redisTemplate.expire(to + ":validateResetPasswordEmailResendTime", 1, TimeUnit.MINUTES);

        Context context = new Context();
        context.setVariable("key", authKey);

        String html = springTemplateEngine.process("resetPasswordEmail.html", context);
        mimeMessageHelper.setText(html, true);

        mailSender.send(mimeMessage);

        return ResponseEntity.status(HttpStatus.OK).body(SignAlertResponseDto.builder()
                .en("A password reset authentication email has been sent successfully.")
                .ko("비밀번호 재설정 인증 이메일이 정상적으로 발송되었습니다.")
                .build());
    }

    @Override
    public ResponseEntity<?> refreshToken(TokenRequestDto tokenRequestDto) {
        if(!jwtTokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("유효하지 않은 Refresh Token");
        }

        String USER_EMAIL = jwtTokenProvider.getUserEmail(tokenRequestDto.getRefreshToken());

        if(!tokenRequestDto.getRefreshToken().equals(redisTemplate.opsForValue().get("REFRESH_TOKEN:" + USER_EMAIL))) {
            throw new RuntimeException("유효하지 않은 Refresh Token");
        }

        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(USER_EMAIL);
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            String REFRESH_TOKEN = jwtTokenProvider.createRefreshToken(userDetails.getUsername());
            String ACCESS_TOKEN = jwtTokenProvider.createAccessToken(userDetails.getUsername(), roles);
            String key = "REFRESH_TOKEN:" + userDetails.getUsername();
            redisTemplate.opsForValue().set(key, REFRESH_TOKEN, 1209600, TimeUnit.SECONDS);

            return ResponseEntity.status(HttpStatus.OK).body(TokenResponseDto.builder()
                    .accessToken(ACCESS_TOKEN)
                    .refreshToken(REFRESH_TOKEN)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<?> validateToken(String accessToken) {
        if(!jwtTokenProvider.validateToken(accessToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
