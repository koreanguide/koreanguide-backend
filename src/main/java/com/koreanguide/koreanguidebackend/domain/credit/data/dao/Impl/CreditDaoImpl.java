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
    private final UserDao userDao;

    public CreditDaoImpl(BankAccountsRepository bankAccountsRepository, CreditLogRepository creditLogRepository,
                         CreditRepository creditRepository, UserDao userDao,
                         CreditReturningRequestRepository creditReturningRequestRepository) {
        this.bankAccountsRepository = bankAccountsRepository;
        this.creditLogRepository = creditLogRepository;
        this.creditRepository = creditRepository;
        this.userDao = userDao;
        this.creditReturningRequestRepository = creditReturningRequestRepository;
    }

    @Override
    public Credit getUserCreditEntity(Long userId) {
        User user = userDao.getUserEntity(userId);

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
    public List<CreditLog> getUserCreditLogEntity(Long userId) {
        return creditLogRepository.findAllByCredit(getUserCreditEntity(userId));
    }

    @Override
    public void saveCreditLogEntity(CreditLog creditLog) {
        creditLogRepository.save(creditLog);
    }

    @Override
    public List<CreditReturningRequest> getUserCreditReturningRequestEntity(Long userId) {
        return creditReturningRequestRepository.getAllByUser(userDao.getUserEntity(userId));
    }

    @Override
    public void saveCreditReturningRequestEntity(CreditReturningRequest creditReturningRequest) {
        creditReturningRequestRepository.save(creditReturningRequest);
    }

    @Override
    public BankAccounts getBankAccountsEntity(Long userId) throws BankAccountsNotFoundException {
        Optional<BankAccounts> bankAccounts = bankAccountsRepository.findBankAccountsByUser(
                userDao.getUserEntity(userId));

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
    public void deleteBankAccountsEntity(Long userId) {
        bankAccountsRepository.delete(
                getBankAccountsEntity(userId)
        );
    }

    @Override
    public void saveBankAccountsEntity(BankAccounts bankAccounts) {
        bankAccountsRepository.save(bankAccounts);
    }
}
