package com.koreanguide.koreanguidebackend.config.rabbitmq;

import com.koreanguide.koreanguidebackend.domain.mail.service.MailService;
import com.koreanguide.koreanguidebackend.domain.mail.task.MailTaskReceiver;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {
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
