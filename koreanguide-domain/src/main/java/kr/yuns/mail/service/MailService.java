package kr.yuns.mail.service;

import org.springframework.http.ResponseEntity;

import jakarta.mail.MessagingException;
import kr.yuns.auth.data.response.SignAlertResponseDto;
import kr.yuns.mail.data.enums.MailType;
import kr.yuns.mail.exception.KeyIncorrectException;
import kr.yuns.mail.exception.MailResendTimeException;

public interface MailService {
    void validateKey(MailType mailType, String targetEmail, String key) throws KeyIncorrectException;
    void processMail(MailType mailType, String targetEmail) throws MessagingException, MailResendTimeException;
    void requestMail(MailType mailType, String targetEmail);
    ResponseEntity<SignAlertResponseDto> sendMail(MailType mailType, String targetEmail) throws MessagingException, MailResendTimeException;
}