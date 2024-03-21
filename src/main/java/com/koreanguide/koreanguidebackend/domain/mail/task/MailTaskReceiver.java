package com.koreanguide.koreanguidebackend.domain.mail.task;

import com.koreanguide.koreanguidebackend.domain.mail.data.enums.MailType;
import com.koreanguide.koreanguidebackend.domain.mail.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import javax.mail.MessagingException;

@RabbitListener(queues = "mail")
@Slf4j
public class MailTaskReceiver {
    private final MailService mailService;

    public MailTaskReceiver(MailService mailService) {
        this.mailService = mailService;
    }

    public MailTask getMailTask(String json) {
        JSONObject jsonObject = new JSONObject(json);
        return MailTask.builder()
                    .email(jsonObject.getString("email"))
                    .mailType(MailType.valueOf(jsonObject.getString("mailType")))
                .build();
    }

    @RabbitHandler
    public void receive(String in) throws InterruptedException, MessagingException {
        log.info("Task received: " + in);
        MailTask mailTask = getMailTask(in);
        try {
            mailService.processMail(mailTask.getMailType(), mailTask.getEmail());
            log.info("Task Process Complete");
        } catch (MessagingException e) {
            log.error("Cannot complete task! : " + e);
        }
    }
}
