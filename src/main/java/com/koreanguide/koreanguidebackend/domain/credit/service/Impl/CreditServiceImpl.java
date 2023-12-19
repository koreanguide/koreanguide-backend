package com.koreanguide.koreanguidebackend.domain.credit.service.Impl;

import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.auth.data.repository.UserRepository;
import com.koreanguide.koreanguidebackend.domain.credit.data.dto.request.TransactionCreditRequestDto;
import com.koreanguide.koreanguidebackend.domain.credit.data.dto.response.CreditHistoryResponseDto;
import com.koreanguide.koreanguidebackend.domain.credit.data.dto.response.CreditResponseDto;
import com.koreanguide.koreanguidebackend.domain.credit.data.entity.Credit;
import com.koreanguide.koreanguidebackend.domain.credit.data.entity.CreditLog;
import com.koreanguide.koreanguidebackend.domain.credit.data.enums.TransactionContent;
import com.koreanguide.koreanguidebackend.domain.credit.data.enums.TransactionType;
import com.koreanguide.koreanguidebackend.domain.credit.data.repository.CreditLogRepository;
import com.koreanguide.koreanguidebackend.domain.credit.data.repository.CreditRepository;
import com.koreanguide.koreanguidebackend.domain.credit.service.CreditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CreditServiceImpl implements CreditService {
    private final CreditRepository creditRepository;
    private final UserRepository userRepository;
    private final CreditLogRepository creditLogRepository;

    @Autowired
    public CreditServiceImpl(CreditRepository creditRepository,
                             UserRepository userRepository,
                             CreditLogRepository creditLogRepository) {
        this.creditRepository = creditRepository;
        this.userRepository = userRepository;
        this.creditLogRepository = creditLogRepository;
    }

    @Override
    public ResponseEntity<CreditResponseDto> checkBalance(Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if(user.isEmpty()) {
            throw new RuntimeException("사용자를 찾을 수 없음");
        }

        Optional<Credit> credit = creditRepository.findByUser(user.get());

        if(credit.isEmpty()) {
            creditRepository.save(Credit.builder()
                    .amount(0L)
                    .recentUsed(LocalDateTime.now())
                    .user(user.get())
                    .build());

            return ResponseEntity.status(HttpStatus.OK).body(CreditResponseDto.builder()
                            .success(true)
                            .msg("정상 처리")
                            .amount(0L)
                    .build());
        }

        return ResponseEntity.status(HttpStatus.OK).body(CreditResponseDto.builder()
                        .success(true)
                        .msg("정상 처리")
                        .amount(credit.get().getAmount())
                .build());
    }

    public String getDetailContent(TransactionContent transactionContent) {
        String DETAIL_CONTENT = "알 수 없음";

        if(transactionContent.equals(TransactionContent.WITHDRAW_TO_ACCOUNT)) {
            DETAIL_CONTENT = "크레딧 계좌 환급";
        }

        return DETAIL_CONTENT;
    }

    public String getTransactionType(TransactionType transactionType) {
        String TRANSACTION_TYPE = "알 수 없음";

        if(transactionType.equals(TransactionType.DEPOSIT)) {
            TRANSACTION_TYPE = "입금";
        } else if (transactionType.equals(TransactionType.WITHDRAW)) {
            TRANSACTION_TYPE = "출금";
        }

        return TRANSACTION_TYPE;
    }

    @Override
    public ResponseEntity<List<CreditHistoryResponseDto>> getCreditHistory(Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if(user.isEmpty()) {
            throw new RuntimeException("사용자를 찾을 수 없음");
        }

        Optional<Credit> credit = creditRepository.findByUser(user.get());
        List<CreditHistoryResponseDto> creditHistoryResponseDtoList = new ArrayList<>();

        if(credit.isEmpty()) {
            creditRepository.save(Credit.builder()
                    .amount(0L)
                    .recentUsed(LocalDateTime.now())
                    .user(user.get())
                    .build());

            return ResponseEntity.status(HttpStatus.OK).body(creditHistoryResponseDtoList);
        } else {
            List<CreditLog> creditLogList = creditLogRepository.findAllByCredit(credit.get());

            for(CreditLog creditLog : creditLogList) {
                creditHistoryResponseDtoList.add(CreditHistoryResponseDto.builder()
                                .content(getDetailContent(creditLog.getTransactionContent()))
                                .transactionType(getTransactionType(creditLog.getTransactionType()))
                                .date(creditLog.getDate())
                                .amount(creditLog.getAmount())
                        .build());
            }

            return ResponseEntity.status(HttpStatus.OK).body(creditHistoryResponseDtoList);
        }
    }

    @Override
    public ResponseEntity<CreditResponseDto> depositCredit(Long userId,
                                                           TransactionCreditRequestDto transactionCreditRequestDto) {
        Optional<User> user = userRepository.findById(userId);
        LocalDateTime localDateTime = LocalDateTime.now();

        if(user.isEmpty()) {
            throw new RuntimeException("사용자를 찾을 수 없음");
        }

        Optional<Credit> credit = creditRepository.findByUser(user.get());

        if(credit.isEmpty()) {
            Credit savedCredit = creditRepository.save(Credit.builder()
                            .amount(transactionCreditRequestDto.getAmount())
                            .recentUsed(localDateTime)
                    .build());

            creditLogRepository.save(CreditLog.builder()
                            .date(localDateTime)
                            .amount(transactionCreditRequestDto.getAmount())
                            .transactionContent(transactionCreditRequestDto.getTransactionContent())
                            .transactionType(TransactionType.DEPOSIT)
                            .credit(savedCredit)
                    .build());
        } else {
            Credit foundCredit = credit.get();
            foundCredit.setAmount(foundCredit.getAmount() + transactionCreditRequestDto.getAmount());
            foundCredit.setRecentUsed(LocalDateTime.now());

            creditRepository.save(foundCredit);

            creditLogRepository.save(CreditLog.builder()
                            .date(localDateTime)
                            .amount(transactionCreditRequestDto.getAmount())
                            .credit(foundCredit)
                            .transactionType(TransactionType.DEPOSIT)
                            .transactionContent(transactionCreditRequestDto.getTransactionContent())
                    .build());
        }

        return ResponseEntity.status(HttpStatus.OK).body(CreditResponseDto.builder()
                        .success(true)
                        .msg("정상 처리")
                        .amount(creditRepository.getByUser(user.get()).getAmount())
                .build());
    }

    @Override
    public ResponseEntity<CreditResponseDto> withdrawCredit(Long userId,
                                                            TransactionCreditRequestDto transactionCreditRequestDto) {
        Optional<User> user = userRepository.findById(userId);

        if(user.isEmpty()) {
            throw new RuntimeException("사용자를 찾을 수 없음");
        }

        Optional<Credit> credit = creditRepository.findByUser(user.get());

        if(credit.isEmpty()) {
            creditRepository.save(Credit.builder()
                    .amount(0L)
                    .recentUsed(LocalDateTime.now())
                    .build());

            return ResponseEntity.status(HttpStatus.OK).body(CreditResponseDto.builder()
                            .success(false)
                            .msg("잔액 부족")
                            .amount(0L)
                    .build());
        } else {
            Credit foundCredit = credit.get();

            if(foundCredit.getAmount() - transactionCreditRequestDto.getAmount() < 0) {
                return ResponseEntity.status(HttpStatus.OK).body(CreditResponseDto.builder()
                                .success(false)
                                .msg("잔액 부족")
                                .amount(foundCredit.getAmount())
                        .build());
            } else {
                foundCredit.setAmount(foundCredit.getAmount() - transactionCreditRequestDto.getAmount());
                foundCredit.setRecentUsed(LocalDateTime.now());

                creditRepository.save(foundCredit);

                creditLogRepository.save(CreditLog.builder()
                                .date(LocalDateTime.now())
                                .amount(transactionCreditRequestDto.getAmount())
                                .transactionType(TransactionType.WITHDRAW)
                                .transactionContent(transactionCreditRequestDto.getTransactionContent())
                                .credit(foundCredit)
                        .build());

                return ResponseEntity.status(HttpStatus.OK).body(CreditResponseDto.builder()
                        .success(true)
                        .msg("정상 처리")
                        .amount(foundCredit.getAmount() - transactionCreditRequestDto.getAmount())
                        .build());
            }
        }
    }
}
