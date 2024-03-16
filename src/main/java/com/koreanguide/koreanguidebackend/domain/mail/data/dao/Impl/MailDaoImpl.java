package com.koreanguide.koreanguidebackend.domain.mail.data.dao.Impl;

import com.koreanguide.koreanguidebackend.domain.mail.data.dao.MailDao;
import com.koreanguide.koreanguidebackend.domain.mail.data.entity.MailLog;
import com.koreanguide.koreanguidebackend.domain.mail.data.repository.MailLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MailDaoImpl implements MailDao {
    private final MailLogRepository mailLogRepository;

    @Autowired
    public MailDaoImpl(MailLogRepository mailLogRepository) {
        this.mailLogRepository = mailLogRepository;
    }

    @Override
    public void saveMailLogEntity(MailLog mailLog) {
        mailLogRepository.save(mailLog);
    }
}
