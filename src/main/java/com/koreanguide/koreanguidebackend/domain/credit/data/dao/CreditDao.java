package com.koreanguide.koreanguidebackend.domain.credit.data.dao;

import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.credit.data.entity.BankAccounts;
import com.koreanguide.koreanguidebackend.domain.credit.data.entity.Credit;
import com.koreanguide.koreanguidebackend.domain.credit.data.entity.CreditLog;
import com.koreanguide.koreanguidebackend.domain.credit.data.entity.CreditReturningRequest;
import com.koreanguide.koreanguidebackend.domain.credit.exception.BankAccountsNotFoundException;

import java.util.List;

public interface CreditDao {
    Credit getUserCreditEntity(User user);
    void saveCreditEntity(Credit credit);
    List<CreditLog> getUserCreditLogEntity(User user);
    void saveCreditLogEntity(CreditLog creditLog);
    List<CreditReturningRequest> getUserCreditReturningRequestEntity(User user);
    void saveCreditReturningRequestEntity(CreditReturningRequest creditReturningRequest);
    BankAccounts getBankAccountsEntity(User user) throws BankAccountsNotFoundException;

    BankAccounts getBankAccountsEntityViaUser(User user) throws BankAccountsNotFoundException;

    void deleteBankAccountsEntity(User user);

    void saveBankAccountsEntity(BankAccounts bankAccounts);
}
