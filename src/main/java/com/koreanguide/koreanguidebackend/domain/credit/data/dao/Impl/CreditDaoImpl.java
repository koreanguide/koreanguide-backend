package com.koreanguide.koreanguidebackend.domain.credit.data.dao.Impl;

import com.koreanguide.koreanguidebackend.domain.auth.data.dao.UserDao;
import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.credit.data.dao.CreditDao;
import com.koreanguide.koreanguidebackend.domain.credit.data.entity.BankAccounts;
import com.koreanguide.koreanguidebackend.domain.credit.data.entity.Credit;
import com.koreanguide.koreanguidebackend.domain.credit.data.entity.CreditLog;
import com.koreanguide.koreanguidebackend.domain.credit.data.entity.CreditReturningRequest;
import com.koreanguide.koreanguidebackend.domain.credit.data.repository.BankAccountsRepository;
import com.koreanguide.koreanguidebackend.domain.credit.data.repository.CreditLogRepository;
import com.koreanguide.koreanguidebackend.domain.credit.data.repository.CreditRepository;
import com.koreanguide.koreanguidebackend.domain.credit.data.repository.CreditReturningRequestRepository;
import com.koreanguide.koreanguidebackend.domain.credit.exception.BankAccountsNotFoundException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class CreditDaoImpl implements CreditDao {
    private final BankAccountsRepository bankAccountsRepository;
    private final CreditLogRepository creditLogRepository;
    private final CreditRepository creditRepository;
    private final CreditReturningRequestRepository creditReturningRequestRepository;

    public CreditDaoImpl(BankAccountsRepository bankAccountsRepository, CreditLogRepository creditLogRepository,
                         CreditRepository creditRepository,
                         CreditReturningRequestRepository creditReturningRequestRepository) {
        this.bankAccountsRepository = bankAccountsRepository;
        this.creditLogRepository = creditLogRepository;
        this.creditRepository = creditRepository;
        this.creditReturningRequestRepository = creditReturningRequestRepository;
    }

    @Override
    public Credit getUserCreditEntity(User user) {
        Optional<Credit> credit = creditRepository.findByUser(user);

        return credit.orElseGet(() -> creditRepository.save(Credit.builder()
                .amount(0L)
                .recentUsed(LocalDateTime.now())
                .user(user)
                .build()));
    }

    @Override
    public void saveCreditEntity(Credit credit) {
        creditRepository.save(credit);
    }

    @Override
    public List<CreditLog> getUserCreditLogEntity(User user) {
        return creditLogRepository.findAllByCredit(getUserCreditEntity(user));
    }

    @Override
    public void saveCreditLogEntity(CreditLog creditLog) {
        creditLogRepository.save(creditLog);
    }

    @Override
    public List<CreditReturningRequest> getUserCreditReturningRequestEntity(User user) {
        return creditReturningRequestRepository.getAllByUser(user);
    }

    @Override
    public void saveCreditReturningRequestEntity(CreditReturningRequest creditReturningRequest) {
        creditReturningRequestRepository.save(creditReturningRequest);
    }

    @Override
    public BankAccounts getBankAccountsEntity(User user) throws BankAccountsNotFoundException {
        Optional<BankAccounts> bankAccounts = bankAccountsRepository.findBankAccountsByUser(user);

        if(bankAccounts.isEmpty()) {
            throw new BankAccountsNotFoundException();
        }

        return bankAccounts.get();
    }

    @Override
    public BankAccounts getBankAccountsEntityViaUser(User user) throws BankAccountsNotFoundException {
        Optional<BankAccounts> bankAccounts = bankAccountsRepository.findBankAccountsByUser(user);

        if(bankAccounts.isEmpty()) {
            throw new BankAccountsNotFoundException();
        }

        return bankAccounts.get();
    }

    @Override
    public void deleteBankAccountsEntity(User user) {
        bankAccountsRepository.delete(
                getBankAccountsEntity(user)
        );
    }

    @Override
    public void saveBankAccountsEntity(BankAccounts bankAccounts) {
        bankAccountsRepository.save(bankAccounts);
    }
}
