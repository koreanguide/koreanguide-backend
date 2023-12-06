package com.koreanguide.koreanguidebackend.domain.credit.service.Impl;

import com.koreanguide.koreanguidebackend.domain.auth.data.dto.response.BaseResponseDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.auth.data.repository.UserRepository;
import com.koreanguide.koreanguidebackend.domain.credit.data.dto.request.BankAccountApplyRequestDto;
import com.koreanguide.koreanguidebackend.domain.credit.data.dto.response.CreditReturningRequestResponseDto;
import com.koreanguide.koreanguidebackend.domain.credit.data.entity.BankAccounts;
import com.koreanguide.koreanguidebackend.domain.credit.data.entity.Credit;
import com.koreanguide.koreanguidebackend.domain.credit.data.entity.CreditLog;
import com.koreanguide.koreanguidebackend.domain.credit.data.entity.CreditReturningRequest;
import com.koreanguide.koreanguidebackend.domain.credit.data.enums.ReturningStatus;
import com.koreanguide.koreanguidebackend.domain.credit.data.enums.TransactionContent;
import com.koreanguide.koreanguidebackend.domain.credit.data.enums.TransactionType;
import com.koreanguide.koreanguidebackend.domain.credit.data.repository.BankAccountsRepository;
import com.koreanguide.koreanguidebackend.domain.credit.data.repository.CreditLogRepository;
import com.koreanguide.koreanguidebackend.domain.credit.data.repository.CreditRepository;
import com.koreanguide.koreanguidebackend.domain.credit.data.repository.CreditReturningRequestRepository;
import com.koreanguide.koreanguidebackend.domain.credit.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {
    private final BankAccountsRepository bankAccountsRepository;
    private final UserRepository userRepository;
    private final CreditReturningRequestRepository creditReturningRequestRepository;
    private final CreditRepository creditRepository;
    private final CreditLogRepository creditLogRepository;

    @Autowired
    public AccountServiceImpl(BankAccountsRepository bankAccountsRepository, UserRepository userRepository,
                              CreditReturningRequestRepository creditReturningRequestRepository,
                              CreditRepository creditRepository, CreditLogRepository creditLogRepository) {
        this.bankAccountsRepository = bankAccountsRepository;
        this.userRepository = userRepository;
        this.creditReturningRequestRepository = creditReturningRequestRepository;
        this.creditRepository = creditRepository;
        this.creditLogRepository = creditLogRepository;
    }

    @Override
    public ResponseEntity<BaseResponseDto> requestReturningToAccount(Long userId, Long amount) {
        Optional<User> user = userRepository.findById(userId);

        if(user.isEmpty()) {
            throw new RuntimeException("사용자를 찾을 수 없음");
        }

        if(amount < 100000) {
            throw new RuntimeException("100,000 크레딧 이상부터 출금 신청이 가능합니다.");
        }

        Credit credit = creditRepository.getByUser(user.get());

        if(credit.getAmount() < amount) {
            return ResponseEntity.status(HttpStatus.OK).body(BaseResponseDto.builder()
                            .success(false)
                            .msg("크레딧 잔액 부족")
                    .build());
        } else {
            credit.setAmount(credit.getAmount() - amount);
            creditRepository.save(credit);

            creditLogRepository.save(CreditLog.builder()
                            .date(LocalDateTime.now())
                            .credit(credit)
                            .transactionContent(TransactionContent.WITHDRAW_TO_ACCOUNT)
                            .transactionType(TransactionType.WITHDRAW)
                    .build());

            creditReturningRequestRepository.save(CreditReturningRequest.builder()
                            .requestDate(LocalDateTime.now())
                            .returningStatus(ReturningStatus.PENDING)
                            .amount(amount)
                            .user(user.get())
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
        Optional<User> user = userRepository.findById(userId);

        if(user.isEmpty()) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        List<CreditReturningRequestResponseDto> returningHistoryList = new ArrayList<>();
        List<CreditReturningRequest> creditReturningRequests = creditReturningRequestRepository.getAllByUser(user.get());

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
    public ResponseEntity<BaseResponseDto> applyBankAccount(Long userId,
                                                            BankAccountApplyRequestDto bankAccountApplyRequestDto) {
        Optional<User> user = userRepository.findById(userId);

        try {
            if(user.isEmpty()) {
                throw new RuntimeException("사용자를 찾을 수 없음");
            }

            Optional<BankAccounts> bankAccounts = bankAccountsRepository.findBankAccountsByUser(user.get());

            if(bankAccounts.isPresent()) {
                bankAccountsRepository.save(BankAccounts.builder()
                        .accountProvider(bankAccountApplyRequestDto.getBankAccountProvider())
                        .accountNumber(bankAccountApplyRequestDto.getBankAccountNumber())
                        .accountHolderName(bankAccountApplyRequestDto.getName())
                        .verified(true)
                        .user(user.get())
                        .appliedAt(LocalDateTime.now())
                        .build());
            } else {
                BankAccounts foundBankAccounts = bankAccounts.get();
                foundBankAccounts.setAccountProvider(bankAccountApplyRequestDto.getBankAccountProvider());
                foundBankAccounts.setAccountHolderName(bankAccountApplyRequestDto.getName());
                foundBankAccounts.setAccountNumber(bankAccountApplyRequestDto.getBankAccountNumber());
                foundBankAccounts.setAppliedAt(LocalDateTime.now());

                bankAccountsRepository.save(foundBankAccounts);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseDto.builder()
                        .success(true)
                        .msg("계좌 등록이 완료되었습니다.")
                .build());
    }

    @Override
    public ResponseEntity<BaseResponseDto> removeBankAccount(Long userId) {
        try {
            Optional<User> user = userRepository.findById(userId);

            if(user.isEmpty()) {
                throw new RuntimeException("사용자를 찾을 수 없음");
            }

            Optional<BankAccounts> bankAccounts = bankAccountsRepository.findBankAccountsByUser(user.get());

            if(bankAccounts.isEmpty()) {
                throw new RuntimeException("등록된 계좌가 없음");
            } else {
                bankAccountsRepository.delete(bankAccounts.get());
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseDto.builder()
                        .success(true)
                        .msg("등록된 계좌 삭제가 완료되었습니다.")
                .build());
    }
}
