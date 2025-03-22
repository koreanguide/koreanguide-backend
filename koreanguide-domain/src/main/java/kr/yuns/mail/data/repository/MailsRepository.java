package kr.yuns.mail.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.yuns.mail.data.entity.Mails;

public interface MailsRepository extends JpaRepository<Mails, Long> {
    
}