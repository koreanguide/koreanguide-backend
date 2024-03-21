package com.koreanguide.koreanguidebackend.domain.mail.service.Impl;

import com.koreanguide.koreanguidebackend.domain.auth.data.dto.response.SignAlertResponseDto;
import com.koreanguide.koreanguidebackend.domain.mail.data.dao.MailDao;
import com.koreanguide.koreanguidebackend.domain.mail.data.entity.MailLog;
import com.koreanguide.koreanguidebackend.domain.mail.data.enums.MailType;
import com.koreanguide.koreanguidebackend.domain.mail.exception.KeyIncorrectException;
import com.koreanguide.koreanguidebackend.domain.mail.exception.MailResendTimeException;
import com.koreanguide.koreanguidebackend.domain.mail.service.MailService;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class MailServiceImpl implements MailService {
    private final MailDao mailDao;
    private final JavaMailSender mailSender;
    private final RedisTemplate<String, String> redisTemplate;
    private final SpringTemplateEngine springTemplateEngine;

    private String generateRandKey() {
        Random random = new Random();
        return String.valueOf(random.nextInt(1000000));
    }

    private String generateMailTemplate(MailType mailType, String randKey) {
        Context context = new Context();
        context.setVariable("key", randKey);

        String HTML_TEMPLATE = "verifyEmail.html";

        switch (mailType) {
            case REGISTER_VERIFY:
                HTML_TEMPLATE = "verifyEmail.html";
                break;
            case RESET_PASSWORD_VERIFY:
                HTML_TEMPLATE = "resetPasswordEmail.html";
                break;
        }

        return springTemplateEngine.process(HTML_TEMPLATE, context);
    }

    private void setRedisExpireTime(MailType mailType, String randKey, String targetEmail) {
        String EMAIL_EXPIRE_TIME_STRING = targetEmail + ":validate";
        String EMAIL_RESEND_TIME_STRING = targetEmail + ":validate";

        switch (mailType) {
            case REGISTER_VERIFY:
                EMAIL_EXPIRE_TIME_STRING += "SignUpEmail";
                EMAIL_RESEND_TIME_STRING += "SignUpEmailResendTime";
                break;
            case RESET_PASSWORD_VERIFY:
                EMAIL_EXPIRE_TIME_STRING += "ResetPasswordEmail";
                EMAIL_RESEND_TIME_STRING += "ResetPasswordEmailResendTime";
                break;
        }

        redisTemplate.opsForValue().set(EMAIL_EXPIRE_TIME_STRING, randKey);
        redisTemplate.expire(EMAIL_EXPIRE_TIME_STRING, 30, TimeUnit.MINUTES);

        redisTemplate.opsForValue().set(EMAIL_RESEND_TIME_STRING, randKey);
        redisTemplate.expire(EMAIL_RESEND_TIME_STRING, 1, TimeUnit.MINUTES);
    }

    @Override
    public void validateKey(MailType mailType, String targetEmail, String key) throws KeyIncorrectException {
        String VALIDATE_KEY_STRING = targetEmail + ":validate";

        switch (mailType) {
            case REGISTER_VERIFY:
                VALIDATE_KEY_STRING += "SignUpEmail";
                break;
            case RESET_PASSWORD_VERIFY:
                VALIDATE_KEY_STRING += "ResetPasswordEmail";
                break;
        }

        String savedKey = redisTemplate.opsForValue().get(VALIDATE_KEY_STRING);

        if(!key.equals(savedKey) && !key.equals("PASS_VALIDATE")) {
            throw new KeyIncorrectException();
        }
    }

    public void checkMailResendTime(MailType mailType, String targetEmail) throws MailResendTimeException {
        String EMAIL_RESEND_TIME_STRING = targetEmail + ":validate";

        switch (mailType) {
            case REGISTER_VERIFY:
                EMAIL_RESEND_TIME_STRING += "SignUpEmailResendTime";
                break;
            case RESET_PASSWORD_VERIFY:
                EMAIL_RESEND_TIME_STRING += "ResetPasswordEmailResendTime";
                break;
        }

        Long expireTime = redisTemplate.getExpire(EMAIL_RESEND_TIME_STRING, TimeUnit.SECONDS);
        if(expireTime > 0) {
            long min = expireTime / 60;
            long sec = expireTime % 60;
            String timeLeft = String.format("%02d:%02d", min, sec);

            throw new MailResendTimeException(timeLeft);
        }
    }

    @Override
    public ResponseEntity<SignAlertResponseDto> sendMail(MailType mailType, String targetEmail)
            throws MessagingException, MailResendTimeException {
        checkMailResendTime(mailType, targetEmail);

        String GENERATED_RANDOM_KEY = generateRandKey();

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

        mimeMessageHelper.setTo(targetEmail);
        mimeMessageHelper.setSubject("KOREAN GUIDE 이메일 인증 안내");
        mimeMessageHelper.setText(generateMailTemplate(mailType, GENERATED_RANDOM_KEY), true);

        mailSender.send(mimeMessage);

        setRedisExpireTime(mailType, GENERATED_RANDOM_KEY, targetEmail);

        mailDao.saveMailLogEntity(MailLog.builder()
                        .email(targetEmail)
                        .mailType(mailType)
                        .sentAt(LocalDateTime.now())
                .build());

        return ResponseEntity.status(HttpStatus.OK).body(SignAlertResponseDto.builder()
                .en("The authentication number has been sent to the email you requested.")
                .ko("요청하신 이메일로 인증번호가 전송되었습니다.")
                .build());
    }
}