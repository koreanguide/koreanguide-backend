package kr.yuns.credit.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.yuns.auth.data.entity.User;
import kr.yuns.credit.data.entity.Credit;

import java.util.Optional;

public interface CreditRepository extends JpaRepository<Credit, Long> {
    Optional<Credit> findByUser(User user);
    Credit getByUser(User user);
}