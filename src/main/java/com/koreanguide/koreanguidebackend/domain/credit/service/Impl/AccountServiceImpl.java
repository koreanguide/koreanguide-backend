package com.koreanguide.koreanguidebackend.domain.credit.service.Impl;

import com.koreanguide.koreanguidebackend.domain.auth.data.dao.UserDao;
import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.credit.data.dao.CreditDao;
import com.koreanguide.koreanguidebackend.domain.credit.data.dto.request.BankAccountApplyRequestDto;
import com.koreanguide.koreanguidebackend.domain.credit.data.dto.response.BankAccountResponseDto;
import com.koreanguide.koreanguidebackend.domain.credit.data.dto.response.CreditReturningRequestResponseDto;
import com.koreanguide.koreanguidebackend.domain.credit.data.entity.BankAccounts;
import com.koreanguide.koreanguidebackend.domain.credit.data.entity.Credit;
import com.koreanguide.koreanguidebackend.domain.credit.data.entity.CreditLog;
import com.koreanguide.koreanguidebackend.domain.credit.data.entity.CreditReturningRequest;
import com.koreanguide.koreanguidebackend.domain.credit.data.enums.ReturningStatus;
import com.koreanguide.koreanguidebackend.domain.credit.data.enums.TransactionContent;
import com.koreanguide.koreanguidebackend.domain.credit.data.enums.TransactionType;
import com.koreanguide.koreanguidebackend.domain.credit.exception.BankAccountsNotFoundException;
import com.koreanguide.koreanguidebackend.domain.credit.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {
    private final CreditDao creditDao;
    private final UserDao userDao;

    @Autowired
    public AccountServiceImpl(CreditDao creditDao, UserDao userDao) {
        this.creditDao = creditDao;
        this.userDao = userDao;
    }

    @Override
    public ResponseEntity<?> requestReturningToAccount(Long userId, Long amount) {
        if(amount < 100000) {
            //        100,000 크레딧 이상 출금 신청 가능
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }

        User user = userDao.getUserEntity(userId);
        Credit credit = creditDao.getUserCreditEntity(user);

        if(credit.getAmount() < amount) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else {
            List<CreditReturningRequest> creditReturningRequestList = creditDao.getUserCreditReturningRequestEntity(
                    user);

            for(CreditReturningRequest creditReturningRequest : creditReturningRequestList) {
                if(creditReturningRequest.getReturningStatus().equals(ReturningStatus.PENDING)) {
                    return ResponseEntity.status(HttpStatus.LOCKED).build();
                }
            }

            credit.setAmount(credit.getAmount() - amount);
            creditDao.saveCreditEntity(credit);

            creditDao.saveCreditLogEntity(CreditLog.builder()
                            .date(LocalDateTime.now())
                            .credit(credit)
                            .amount(credit.getAmount())
                            .transactionContent(TransactionContent.WITHDRAW_TO_ACCOUNT)
                            .transactionType(TransactionType.WITHDRAW)
                    .build());

            creditDao.saveCreditReturningRequestEntity(CreditReturningRequest.builder()
                            .requestDate(LocalDateTime.now())
                            .returningStatus(ReturningStatus.PENDING)
                            .amount(amount)
                            .user(credit.getUser())
                            .updateDate(null)
                            .credit(credit)
                    .build());

            return ResponseEntity.status(HttpStatus.OK).build();
        }
    }

    @Override
    public ResponseEntity<List<CreditReturningRequestResponseDto>> getReturningHistory(Long userId) {
        List<CreditReturningRequestResponseDto> returningHistoryList = new ArrayList<>();
        User user = userDao.getUserEntity(userId);
        List<CreditReturningRequest> creditReturningRequests = creditDao.getUserCreditReturningRequestEntity(user);

        for(CreditReturningRequest creditReturningRequest : creditReturningRequests) {
            returningHistoryList.add(CreditReturningRequestResponseDto.builder()
                            .returningStatus(creditReturningRequest.getReturningStatus())
                            .amount(creditReturningRequest.getAmount())
                            .requestDate(creditReturningRequest.getRequestDate())
                            .updateDate(creditReturningRequest.getUpdateDate())
                    .build());
        }

        return ResponseEntity.status(HttpStatus.OK).body(returningHistoryList);
    }

    @Override
    public ResponseEntity<?> getRecentReturningDay(Long userId) {
        User user = userDao.getUserEntity(userId);
        List<CreditReturningRequest> creditReturningRequestList =
                creditDao.getUserCreditReturningRequestEntity(user);

        if(creditReturningRequestList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body("최근 환급 요청 내역이 존재하지 않습니다.");
        } else {
            CreditReturningRequest creditReturningRequest = creditReturningRequestList.get(creditReturningRequestList.size() - 1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일, a h시 m분");
            String formattedDateTime = creditReturningRequest.getRequestDate().format(formatter);

            if(creditReturningRequest.getReturningStatus().equals(ReturningStatus.PENDING)) {
                return ResponseEntity.status(HttpStatus.OK).body(formattedDateTime + "에 요청된 환급 승인 대기 중");
            } else if (creditReturningRequest.getReturningStatus().equals(ReturningStatus.ACCEPTED)) {
                return ResponseEntity.status(HttpStatus.OK).body(formattedDateTime + "에 요청된 환급 요청이 승인 됨");
            } else if (creditReturningRequest.getReturningStatus().equals(ReturningStatus.REJECTED)) {
                return ResponseEntity.status(HttpStatus.OK).body(formattedDateTime + "에 요청된 환급 요청이 거절 됨");
            } else {
                return ResponseEntity.status(HttpStatus.OK).body("최근 요청을 불러올 수 없음");
            }
        }
    }

    @Override
    public ResponseEntity<?> getBankAccount(Long userId) {
        User user = userDao.getUserEntity(userId);
        BankAccounts bankAccounts = creditDao.getBankAccountsEntity(user);

        return ResponseEntity.status(HttpStatus.OK).body(BankAccountResponseDto.builder()
                        .accountHolderName(bankAccounts.getAccountHolderName())
                        .accountNumber(bankAccounts.getAccountNumber())
                        .accountProvider(bankAccounts.getAccountProvider())
                .build());
    }

    @Override
    public ResponseEntity<?> applyBankAccount(Long userId,
                                                            BankAccountApplyRequestDto bankAccountApplyRequestDto) {
        User user = userDao.getUserEntity(userId);
        try {
            BankAccounts bankAccounts = creditDao.getBankAccountsEntity(user);
            bankAccounts.setAccountNumber(bankAccountApplyRequestDto.getBankAccountNumber());
            bankAccounts.setAccountProvider(bankAccountApplyRequestDto.getBankAccountProvider());
            bankAccounts.setAppliedAt(LocalDateTime.now());

            creditDao.saveBankAccountsEntity(bankAccounts);
        } catch (BankAccountsNotFoundException e) {
            creditDao.saveBankAccountsEntity(BankAccounts.builder()
                        .accountProvider(bankAccountApplyRequestDto.getBankAccountProvider())
                        .accountNumber(bankAccountApplyRequestDto.getBankAccountNumber())
                        .accountHolderName(bankAccountApplyRequestDto.getName())
                        .verified(true)
                        .user(userDao.getUserEntity(userId))
                        .appliedAt(LocalDateTime.now())
                    .build());
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<?> removeBankAccount(Long userId) {
        User user = userDao.getUserEntity(userId);
        creditDao.deleteBankAccountsEntity(user);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
