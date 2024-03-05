package com.koreanguide.koreanguidebackend.domain.credit.service.Impl;

import com.koreanguide.koreanguidebackend.domain.credit.data.dao.CreditDao;
import com.koreanguide.koreanguidebackend.domain.credit.data.dto.request.TransactionCreditRequestDto;
import com.koreanguide.koreanguidebackend.domain.credit.data.dto.response.CreditHistoryResponseDto;
import com.koreanguide.koreanguidebackend.domain.credit.data.dto.response.CreditResponseDto;
import com.koreanguide.koreanguidebackend.domain.credit.data.entity.Credit;
import com.koreanguide.koreanguidebackend.domain.credit.data.entity.CreditLog;
import com.koreanguide.koreanguidebackend.domain.credit.data.enums.TransactionContent;
import com.koreanguide.koreanguidebackend.domain.credit.data.enums.TransactionType;
import com.koreanguide.koreanguidebackend.domain.credit.service.CreditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CreditServiceImpl implements CreditService {
    private final CreditDao creditDao;

    @Autowired
    public CreditServiceImpl(CreditDao creditDao) {
        this.creditDao = creditDao;
    }

    @Override
    public ResponseEntity<CreditResponseDto> checkBalance(Long userId) {
        Credit credit = creditDao.getUserCreditEntity(userId);

        return ResponseEntity.status(HttpStatus.OK).body(CreditResponseDto.builder()
                        .success(true)
                        .msg("정상 처리")
                        .amount(credit.getAmount())
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
        List<CreditLog> creditLogList = creditDao.getUserCreditLogEntity(userId);
        List<CreditHistoryResponseDto> creditHistoryResponseDtoList = new ArrayList<>();

        for(CreditLog creditLog : creditLogList) {
            NumberFormat formatter = NumberFormat.getNumberInstance();
            creditHistoryResponseDtoList.add(CreditHistoryResponseDto.builder()
                            .content(getDetailContent(creditLog.getTransactionContent()))
                            .transactionType(getTransactionType(creditLog.getTransactionType()))
                            .date(creditLog.getDate())
                            .amount(
                                    creditLog.getTransactionType().equals(TransactionType.WITHDRAW) ?
                                            "-" + formatter.format(creditLog.getAmount()) :
                                            "+" + formatter.format(creditLog.getAmount())
                            )
                    .build());
        }

        return ResponseEntity.status(HttpStatus.OK).body(creditHistoryResponseDtoList);
    }

    @Override
    public ResponseEntity<CreditResponseDto> depositCredit(Long userId,
                                                           TransactionCreditRequestDto transactionCreditRequestDto) {
        LocalDateTime localDateTime = LocalDateTime.now();
        Credit credit = creditDao.getUserCreditEntity(userId);

        credit.setAmount(credit.getAmount() + transactionCreditRequestDto.getAmount());
        credit.setRecentUsed(LocalDateTime.now());

        creditDao.saveCreditEntity(credit);

        creditDao.saveCreditLogEntity(CreditLog.builder()
                .date(localDateTime)
                .amount(transactionCreditRequestDto.getAmount())
                .credit(credit)
                .transactionType(TransactionType.DEPOSIT)
                .transactionContent(transactionCreditRequestDto.getTransactionContent())
                .build());

        return ResponseEntity.status(HttpStatus.OK).body(CreditResponseDto.builder()
                        .success(true)
                        .msg("정상 처리")
                        .amount(credit.getAmount())
                .build());
    }

    @Override
    public ResponseEntity<CreditResponseDto> withdrawCredit(Long userId,
                                                            TransactionCreditRequestDto transactionCreditRequestDto) {
        Credit credit = creditDao.getUserCreditEntity(userId);

        if(credit.getAmount() - transactionCreditRequestDto.getAmount() < 0) {
            return ResponseEntity.status(HttpStatus.OK).body(CreditResponseDto.builder()
                            .success(false)
                            .msg("잔액 부족")
                            .amount(credit.getAmount())
                    .build());
        } else {
            credit.setAmount(credit.getAmount() - transactionCreditRequestDto.getAmount());
            credit.setRecentUsed(LocalDateTime.now());

            creditDao.saveCreditEntity(credit);

            creditDao.saveCreditLogEntity(CreditLog.builder()
                            .date(LocalDateTime.now())
                            .amount(transactionCreditRequestDto.getAmount())
                            .transactionType(TransactionType.WITHDRAW)
                            .transactionContent(transactionCreditRequestDto.getTransactionContent())
                            .credit(credit)
                    .build());

            return ResponseEntity.status(HttpStatus.OK).body(CreditResponseDto.builder()
                    .success(true)
                    .msg("정상 처리")
                    .amount(credit.getAmount())
                    .build());
        }
    }
}
