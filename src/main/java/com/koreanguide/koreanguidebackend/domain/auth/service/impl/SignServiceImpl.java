package com.koreanguide.koreanguidebackend.domain.auth.service.impl;

import com.koreanguide.koreanguidebackend.config.security.JwtTokenProvider;
import com.koreanguide.koreanguidebackend.domain.auth.data.dao.UserDao;
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
import com.koreanguide.koreanguidebackend.domain.auth.exception.UserNotFoundException;
import com.koreanguide.koreanguidebackend.domain.auth.service.SignService;
import com.koreanguide.koreanguidebackend.domain.credit.data.dao.CreditDao;
import com.koreanguide.koreanguidebackend.domain.credit.data.entity.Credit;
import com.koreanguide.koreanguidebackend.domain.mail.data.enums.MailType;
import com.koreanguide.koreanguidebackend.domain.mail.exception.KeyIncorrectException;
import com.koreanguide.koreanguidebackend.domain.mail.exception.MailResendTimeException;
import com.koreanguide.koreanguidebackend.domain.mail.service.MailService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class SignServiceImpl implements SignService {
    private final UserDao userDao;
    private final CreditDao creditDao;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final MailService mailService;
    private final RedisTemplate<String, String> redisTemplate;

    private String generateAccessToken(String email, List<String> roles) {
        return jwtTokenProvider.createAccessToken(String.valueOf(email), roles);
    }

    private String generateRefreshToken(String email) {
        return jwtTokenProvider.createRefreshToken(String.valueOf(email));
    }

    private boolean matchEmailPattern(String email) {
        String emailPattern = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        return email.matches(emailPattern);
    }

    @Override
    public ResponseEntity<SignAlertResponseDto> validateKey(MailType mailType, String targetEmail, String key) {
        try {
            mailService.validateKey(mailType, targetEmail, key);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (KeyIncorrectException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    SignAlertResponseDto.builder()
                            .ko("이메일 인증 번호가 일치하지 않습니다.")
                            .en("Email authentication number does not match.")
                            .build());
        }
    }

    @Override
    public ResponseEntity<?> sendVerifyMail(String to) throws MessagingException {
        if(userDao.checkAlreadyExistUserByEmail(to)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(SignAlertResponseDto.builder()
                            .en("This email address is already registered with the service. " +
                                    "Please use a different email address or try to sign-in.")
                            .ko("이미 서비스에 등록된 이메일 주소입니다. 다른 이메일 주소를 사용하거나 로그인을 시도하십시오.")
                    .build());
        }

        if(matchEmailPattern(to)) {
            throw new RuntimeException("이메일 형식 입력 오류");
        }

        try {
            mailService.sendMail(MailType.REGISTER_VERIFY, to);

            return ResponseEntity.status(HttpStatus.OK).body(SignAlertResponseDto.builder()
                    .en("A membership authentication email has been sent normally.")
                    .ko("회원가입 인증 이메일이 정상적으로 발송되었습니다.")
                    .build());
        } catch (MailResendTimeException e) {
            return ResponseEntity.status(HttpStatus.LOCKED).body(SignAlertResponseDto.builder()
                    .en("You can resend the email authentication after " + e.getMessage() + ".")
                    .ko(e.getMessage() + " 후에 이메일 인증 재발송이 가능합니다.")
                    .build());
        }
    }

    @Override
    public ResponseEntity<?> signUp(SignUpRequestDto signUpRequestDto) {
        if (userDao.checkAlreadyExistUserByEmail(signUpRequestDto.getEmail())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    SignAlertResponseDto.builder()
                            .ko("이미 서비스에 등록된 이메일 주소입니다. 다른 이메일 주소를 사용하거나 로그인을 시도하십시오.")
                            .en("This email address is already registered with the service. " +
                                    "Please use a different email address or try to sign-in.")
                            .build()
                    );
        }

        if(userDao.checkAlreadyExistUserByNickname(signUpRequestDto.getNickname())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    SignAlertResponseDto.builder()
                            .ko("사용할 수 없는 닉네임입니다. 다른 닉네임을 선택하십시오.")
                            .en("This is a nickname that cannot be used. Please select a different nickname.")
                            .build()
            );
        }

        try {
            mailService.validateKey(MailType.REGISTER_VERIFY, signUpRequestDto.getEmail(), signUpRequestDto.getAuthKey());
        } catch (KeyIncorrectException e) {
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(
                    SignAlertResponseDto.builder()
                            .ko("이메일 인증 유효 시간이 만료되었습니다. 이메일 인증을 다시 시도하십시오.")
                            .en("Email authentication has expired. Please try email authentication again.")
                            .build()
            );
        }

        if(matchEmailPattern(signUpRequestDto.getEmail())) {
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
                .profileUrl("DEFAULT")
                .password(passwordEncoder.encode(signUpRequestDto.getPassword()))
                .roles(Collections.singletonList("ROLE_USER"))
                .createdAt(LocalDateTime.now())
                .lastAccessTime(LocalDateTime.now())
                .build();

        userDao.saveUserEntity(user);
        creditDao.saveCreditEntity(Credit.builder()
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
        try {
            User user = userDao.getUserEntityByEmail(signInRequestDto.getEmail());

            if (!passwordEncoder.matches(signInRequestDto.getPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("이메일 또는 비밀번호가 일치하지 않습니다.");
            }

            user.setLastAccessTime(LocalDateTime.now());

            userDao.saveUserEntity(user);

            String GENERATED_ACCESS_TOKEN = generateAccessToken(user.getEmail(), user.getRoles());
            String GENERATED_REFRESH_TOKEN = generateRefreshToken(user.getEmail());

            String key = "REFRESH_TOKEN:" + user.getEmail();
            redisTemplate.opsForValue().set(key, GENERATED_REFRESH_TOKEN, 1209600, TimeUnit.SECONDS);

            return ResponseEntity.status(HttpStatus.OK).body(SignInResponseDto.builder()
                    .isGuide(user.getUserRole().equals(UserRole.GUIDE))
                    .accessToken(GENERATED_ACCESS_TOKEN)
                    .refreshToken(GENERATED_REFRESH_TOKEN)
                    .name(user.getNickname())
                    .email(user.getEmail())
                    .build());
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("등록되지 않은 회원입니다.");
        }
    }

    @Override
    public ResponseEntity<?> resetPassword(ResetPasswordRequestDto resetPasswordRequestDto) {
        if(matchEmailPattern(resetPasswordRequestDto.getEmail())) {
            throw new RuntimeException("이메일 형식 입력 오류");
        }

        try {
            mailService.validateKey(MailType.RESET_PASSWORD_VERIFY, resetPasswordRequestDto.getEmail(),
                    resetPasswordRequestDto.getValidateKey());
        } catch (KeyIncorrectException e) {
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

        try {
            User user = userDao.getUserEntityByEmail(resetPasswordRequestDto.getEmail());

            if(passwordEncoder.matches(resetPasswordRequestDto.getPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.LOCKED).body(SignAlertResponseDto.builder()
                        .en("The new password is not different from the current password.")
                        .ko("새로운 비밀번호가 현재 비밀번호와 다르지 않습니다.")
                        .build());
            }

            user.setPassword(passwordEncoder.encode(resetPasswordRequestDto.getPassword()));

            userDao.saveUserEntity(user);

            return ResponseEntity.status(HttpStatus.OK).body(SignAlertResponseDto.builder()
                    .ko("비밀번호 변경이 정상적으로 완료되었습니다.")
                    .en("The password change has been completed successfully.")
                    .build());
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(SignAlertResponseDto.builder()
                    .ko("입력한 이메일 주소로 등록된 회원을 찾을 수 없습니다. 이메일 주소를 다시 한 번 확인하십시오.")
                    .en("No registered members could be found with the email address you entered. " +
                            "Please double check your email address.")
                    .build());
        }
    }

    @Override
    public ResponseEntity<?> sendResetPasswordVerifyMail(String to) throws MessagingException {
        if(matchEmailPattern(to)) {
            throw new RuntimeException("이메일 형식 입력 오류");
        }

        try {
            mailService.sendMail(MailType.RESET_PASSWORD_VERIFY, to);

            return ResponseEntity.status(HttpStatus.OK).body(SignAlertResponseDto.builder()
                    .en("A password reset authentication email has been sent successfully.")
                    .ko("비밀번호 재설정 인증 이메일이 정상적으로 발송되었습니다.")
                    .build());
        } catch (MailResendTimeException e) {
            return ResponseEntity.status(HttpStatus.LOCKED).body(SignAlertResponseDto.builder()
                    .en("You can resend the email authentication after " + e.getMessage() + ".")
                    .ko(e.getMessage() + " 후에 이메일 인증 재발송이 가능합니다.")
                    .build());
        }
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
    public ResponseEntity<?> validateToken(TokenRequestDto tokenRequestDto) {
        if(!jwtTokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
