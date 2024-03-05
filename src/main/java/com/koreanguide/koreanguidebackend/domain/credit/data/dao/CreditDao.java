package com.koreanguide.koreanguidebackend.domain.credit.data.dao;

import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.credit.data.entity.BankAccounts;
import com.koreanguide.koreanguidebackend.domain.credit.data.entity.Credit;
import com.koreanguide.koreanguidebackend.domain.credit.data.entity.CreditLog;
import com.koreanguide.koreanguidebackend.domain.credit.data.entity.CreditReturningRequest;
import com.koreanguide.koreanguidebackend.domain.credit.exception.BankAccountsNotFoundException;

import java.util.List;

public interface CreditDao {
    Credit getUserCreditEntity(Long userId);
    void saveCreditEntity(Credit credit);
    List<CreditLog> getUserCreditLogEntity(Long userId);
    void saveCreditLogEntity(CreditLog creditLog);
    List<CreditReturningRequest> getUserCreditReturningRequestEntity(Long userId);
    void saveCreditReturningRequestEntity(CreditReturningRequest creditReturningRequest);
    BankAccounts getBankAccountsEntity(Long userId) throws BankAccountsNotFoundException;

    BankAccounts getBankAccountsEntityViaUser(User user) throws BankAccountsNotFoundException;

    void deleteBankAccountsEntity(Long userId);

    void saveBankAccountsEntity(BankAccounts bankAccounts);
}
