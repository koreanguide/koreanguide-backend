package kr.yuns.mail.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import jakarta.mail.MessagingException;
import kr.yuns.mail.data.enums.MailType;
import kr.yuns.mail.service.MailService;

@RabbitListener(queues = "mail")
@RequiredArgsConstructor
@Slf4j
public class MailTaskReceiver {
    private final MailService mailService;

    public MailTask getMailTask(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        return MailTask.builder()
                    .email(jsonObject.getString("email"))
                    .mailType(MailType.valueOf(jsonObject.getString("mailType")))
                .build();
    }

    @RabbitHandler
    public void receive(String in) throws InterruptedException, MessagingException, JSONException {
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