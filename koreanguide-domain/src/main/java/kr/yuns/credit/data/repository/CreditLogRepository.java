package kr.yuns.credit.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.yuns.credit.data.entity.Credit;
import kr.yuns.credit.data.entity.CreditLog;

import java.util.List;

public interface CreditLogRepository extends JpaRepository<CreditLog, Long> {
    List<CreditLog> findAllByCredit(Credit credit);
}