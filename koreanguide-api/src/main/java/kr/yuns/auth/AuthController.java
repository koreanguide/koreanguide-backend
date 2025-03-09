package kr.yuns.auth;

import kr.yuns.auth.data.request.SignInRequestDto;
import kr.yuns.auth.data.request.SignUpRequestDto;
import kr.yuns.auth.data.request.TokenRequestDto;
import kr.yuns.auth.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // @PostMapping("/v1/verify/request/pw")
    // public ResponseEntity<?> requestResetPasswordVerifyEmail(
    //         @RequestBody ValidateEmailRequestDto validateEmailRequestDto) throws MessagingException {
    //     return authService.sendResetPasswordVerifyMail(validateEmailRequestDto.getEmail());
    // }

    // @PostMapping("/v1/verify/validate/pw")
    // public ResponseEntity<SignAlertResponseDto> validateResetPasswordEmail(
    //         @RequestBody ValidateRequestDto validateRequestDto) {
    //     return authService.validateKey(
    //             MailType.RESET_PASSWORD_VERIFY, validateRequestDto.getEmail(), validateRequestDto.getKey());
    // }

    // @PutMapping("/v1/reset/password")
    // public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequestDto resetPasswordRequestDto) {
    //     return authService.resetPassword(resetPasswordRequestDto);
    // }

    // @PostMapping("/v1/verify/request")
    // public ResponseEntity<?> requestEmailAuth(@RequestBody ValidateEmailRequestDto validateEmailRequestDto)
    //         throws MessagingException {
    //     return authService.sendVerifyMail(validateEmailRequestDto.getEmail());
    // }

    // @PostMapping("/v2/verify/request")
    // public ResponseEntity<?> requestVerifyMail(@RequestBody ValidateEmailRequestDto validateEmailRequestDto) throws MessagingException {
    //     return authService.requestVerifyMail(validateEmailRequestDto.getEmail());
    // }

    // @PostMapping("/v1/verify/validate")
    // public ResponseEntity<?> validateEmail(@RequestBody ValidateRequestDto validateRequestDto) {
    //     return authService.validateKey(MailType.REGISTER_VERIFY, validateRequestDto.getEmail(),
    //             validateRequestDto.getKey());
    // }

    @PostMapping(value = "/v1/signup")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        return authService.signUp(signUpRequestDto);
    }

    // @PostMapping(value = "/v1/kakao")
    // public ResponseEntity<SignInResponseDto> signWithKakao(@RequestParam String code) throws Exception {
    //     return authService.socialKakaoLogin(code);
    // }

    @PostMapping(value = "/v1/signin")
    public ResponseEntity<?> signIn(@RequestBody SignInRequestDto signInRequestDto) throws RuntimeException {
        return authService.signIn(signInRequestDto);
    }

    @PostMapping(value = "/v1/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRequestDto tokenRequestDto){
        return authService.refreshToken(tokenRequestDto);
    }

    @PostMapping(value = "/v1/token")
    public ResponseEntity<?> validateToken(@RequestBody TokenRequestDto tokenRequestDto) {
        return authService.validateToken(tokenRequestDto);
    }

    @GetMapping(value = "/v1/exception")
    public void exceptionTest() throws RuntimeException{
        throw new RuntimeException("접근이 금지되었습니다.");
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<Map<String, String>> ExceptionHandler(RuntimeException e){
        HttpHeaders responseHeaders = new HttpHeaders();
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        log.info("ExceptionHandler 호출, {}, {}", e.getCause(), e.getMessage());
        Map<String, String> map = new HashMap<>();
        map.put("ERROR_TYPE", httpStatus.getReasonPhrase());
        map.put("ERROR_CODE", "400");
        map.put("ERROR_MSG", e.getMessage());

        return new ResponseEntity<>(map, responseHeaders,httpStatus);
    }
}