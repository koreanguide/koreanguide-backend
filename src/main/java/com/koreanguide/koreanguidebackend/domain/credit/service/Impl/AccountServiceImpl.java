package com.koreanguide.koreanguidebackend.domain.credit.service.Impl;

import com.koreanguide.koreanguidebackend.domain.auth.data.dao.UserDao;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.response.BaseResponseDto;
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
    public ResponseEntity<BaseResponseDto> requestReturningToAccount(Long userId, Long amount) {
        if(amount < 100000) {
            throw new RuntimeException("100,000 크레딧 이상부터 출금 신청이 가능합니다.");
        }

        Credit credit = creditDao.getUserCreditEntity(userId);

        if(credit.getAmount() < amount) {
            return ResponseEntity.status(HttpStatus.OK).body(BaseResponseDto.builder()
                            .success(false)
                            .msg("크레딧 잔액 부족")
                    .build());
        } else {
            List<CreditReturningRequest> creditReturningRequestList = creditDao.getUserCreditReturningRequestEntity(userId);

            for(CreditReturningRequest creditReturningRequest : creditReturningRequestList) {
                if(creditReturningRequest.getReturningStatus().equals(ReturningStatus.PENDING)) {
                    return ResponseEntity.status(HttpStatus.LOCKED).body(BaseResponseDto.builder()
                                    .success(false)
                                    .msg("진행 중인 환급 요청이 존재합니다. 환급 요청이 모두 완료된 후 신청해야 합니다.")
                            .build());
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

            return ResponseEntity.status(HttpStatus.OK).body(BaseResponseDto.builder()
                            .success(true)
                            .msg("지급 요청이 완료되었습니다.")
                    .build());
        }
    }

    @Override
    public ResponseEntity<List<CreditReturningRequestResponseDto>> getReturningHistory(Long userId) {
        List<CreditReturningRequestResponseDto> returningHistoryList = new ArrayList<>();
        List<CreditReturningRequest> creditReturningRequests = creditDao.getUserCreditReturningRequestEntity(userId);

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
        List<CreditReturningRequest> creditReturningRequestList =
                creditDao.getUserCreditReturningRequestEntity(userId);

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
        BankAccounts bankAccounts = creditDao.getBankAccountsEntity(userId);

        return ResponseEntity.status(HttpStatus.OK).body(BankAccountResponseDto.builder()
                        .accountHolderName(bankAccounts.getAccountHolderName())
                        .accountNumber(bankAccounts.getAccountNumber())
                        .accountProvider(bankAccounts.getAccountProvider())
                .build());
    }

    @Override
    public ResponseEntity<BaseResponseDto> applyBankAccount(Long userId,
                                                            BankAccountApplyRequestDto bankAccountApplyRequestDto) {
        try {
            BankAccounts bankAccounts = creditDao.getBankAccountsEntity(userId);
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

        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseDto.builder()
                        .success(true)
                        .msg("계좌 등록이 완료되었습니다.")
                .build());
    }

    @Override
    public ResponseEntity<BaseResponseDto> removeBankAccount(Long userId) {
        creditDao.deleteBankAccountsEntity(userId);

        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseDto.builder()
                        .success(true)
                        .msg("등록된 계좌 삭제가 완료되었습니다.")
                .build());
    }
}
