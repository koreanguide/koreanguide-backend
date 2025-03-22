package kr.yuns.mail.data.dao;

import kr.yuns.mail.data.entity.Mails;

public interface MailDao {
    void saveMailLogEntity(Mails mails);
}