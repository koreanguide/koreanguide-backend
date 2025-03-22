package kr.yuns.credit.data.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.yuns.auth.data.entity.User;
import kr.yuns.credit.data.entity.BankAccounts;

public interface BankAccountsRepository extends JpaRepository<BankAccounts, Long> {
    Optional<BankAccounts> findBankAccountsByUser(User user);
}