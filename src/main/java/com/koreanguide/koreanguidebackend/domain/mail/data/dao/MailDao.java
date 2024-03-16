package com.koreanguide.koreanguidebackend.domain.mail.data.dao;

import com.koreanguide.koreanguidebackend.domain.mail.data.entity.MailLog;

public interface MailDao {
    void saveMailLogEntity(MailLog mailLog);
}
