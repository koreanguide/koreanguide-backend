package kr.yuns.credit.data.dao.Impl;

import org.springframework.stereotype.Component;

import kr.yuns.auth.data.entity.User;
import kr.yuns.credit.data.dao.CreditDao;
import kr.yuns.credit.data.repository.BankAccountsRepository;
import kr.yuns.credit.data.repository.CreditLogRepository;
import kr.yuns.credit.data.repository.CreditRepository;
import kr.yuns.credit.data.repository.CreditReturningRequestRepository;
import kr.yuns.credit.data.entity.BankAccounts;
import kr.yuns.credit.data.entity.Credit;
import kr.yuns.credit.data.entity.CreditLog;
import kr.yuns.credit.data.entity.CreditReturningRequest;
import kr.yuns.credit.data.exception.BankAccountsNotFoundException;

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