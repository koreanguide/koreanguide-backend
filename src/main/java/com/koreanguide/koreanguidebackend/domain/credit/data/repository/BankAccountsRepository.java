package com.koreanguide.koreanguidebackend.domain.credit.data.repository;

import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.credit.data.entity.BankAccounts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankAccountsRepository extends JpaRepository<BankAccounts, Long> {
    Optional<BankAccounts> findBankAccountsByUser(User user);
}
