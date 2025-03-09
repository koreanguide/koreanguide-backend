package kr.yuns.auth.service;

import org.springframework.http.ResponseEntity;

// import jakarta.mail.MessagingException;
import kr.yuns.auth.data.request.SignInRequestDto;
import kr.yuns.auth.data.request.SignUpRequestDto;
import kr.yuns.auth.data.request.TokenRequestDto;
// import kr.yuns.auth.data.response.SignAlertResponseDto;
// import kr.yuns.auth.data.response.SignInResponseDto;

public interface AuthService {
    // ResponseEntity<SignInResponseDto> socialKakaoLogin(String code) throws Exception;
    // ResponseEntity<SignAlertResponseDto> validateKey(MailType mailType, String targetEmail, String key);
    // ResponseEntity<?> sendVerifyMail(String to) throws MessagingException;
    // ResponseEntity<?> requestVerifyMail(String to) throws MessagingException;
    ResponseEntity<?> signUp(SignUpRequestDto signUpRequestDto);
    ResponseEntity<?> signIn(SignInRequestDto signInRequestDto);
    // ResponseEntity<?> resetPassword(ResetPasswordRequestDto resetPasswordRequestDto);
    // ResponseEntity<?> sendResetPasswordVerifyMail(String to) throws MessagingException;
    ResponseEntity<?> refreshToken(TokenRequestDto tokenRequestDto);
    ResponseEntity<?> validateToken(TokenRequestDto tokenRequestDto);
}