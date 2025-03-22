package kr.yuns.mail.data.dao.Impl;

import org.springframework.stereotype.Component;

import kr.yuns.mail.data.dao.MailDao;
import kr.yuns.mail.data.entity.Mails;
import kr.yuns.mail.data.repository.MailsRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MailDaoImpl implements MailDao {
    private final MailsRepository mailsRepository;

    @Override
    public void saveMailLogEntity(Mails mails) {
        mailsRepository.save(mails);
    }
}