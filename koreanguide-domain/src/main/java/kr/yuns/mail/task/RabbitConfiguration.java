package kr.yuns.mail.task;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import kr.yuns.mail.service.MailService;

@Configuration
public class RabbitConfiguration {
    @Bean
    public Queue mail() {
        return new Queue("mail");
    }

    private static class ReceiverConfig {
        @Bean
        public MailTaskReceiver receiver(MailService mailService) {
            return new MailTaskReceiver(mailService);
        }
    }
}