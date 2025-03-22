package kr.yuns.credit.data.dao;

import java.util.List;

import kr.yuns.auth.data.entity.User;
import kr.yuns.credit.data.entity.BankAccounts;
import kr.yuns.credit.data.entity.Credit;
import kr.yuns.credit.data.entity.CreditLog;
import kr.yuns.credit.data.entity.CreditReturningRequest;
import kr.yuns.credit.data.exception.BankAccountsNotFoundException;

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