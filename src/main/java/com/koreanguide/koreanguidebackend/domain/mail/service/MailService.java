package com.koreanguide.koreanguidebackend.domain.mail.service;

import com.koreanguide.koreanguidebackend.domain.auth.data.dto.response.SignAlertResponseDto;
import com.koreanguide.koreanguidebackend.domain.mail.data.enums.MailType;
import com.koreanguide.koreanguidebackend.domain.mail.exception.KeyIncorrectException;
import com.koreanguide.koreanguidebackend.domain.mail.exception.MailResendTimeException;
import org.springframework.http.ResponseEntity;

import javax.mail.MessagingException;

public interface MailService {
    void validateKey(MailType mailType, String targetEmail, String key) throws KeyIncorrectException;

    void processMail(MailType mailType, String targetEmail) throws MessagingException, MailResendTimeException;

    void requestMail(MailType mailType, String targetEmail);

    ResponseEntity<SignAlertResponseDto> sendMail(MailType mailType, String targetEmail)
            throws MessagingException, MailResendTimeException;
}
