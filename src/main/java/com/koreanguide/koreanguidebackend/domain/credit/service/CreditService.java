package com.koreanguide.koreanguidebackend.domain.credit.service;

import com.koreanguide.koreanguidebackend.domain.credit.data.dto.request.TransactionCreditRequestDto;
import com.koreanguide.koreanguidebackend.domain.credit.data.dto.response.CreditHistoryResponseDto;
import com.koreanguide.koreanguidebackend.domain.credit.data.dto.response.CreditResponseDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CreditService {
    ResponseEntity<CreditResponseDto> checkBalance(Long userId);

    ResponseEntity<List<CreditHistoryResponseDto>> getCreditHistory(Long userId);

    ResponseEntity<CreditResponseDto> depositCredit(Long userId,
                                                    TransactionCreditRequestDto transactionCreditRequestDto);

    ResponseEntity<CreditResponseDto> withdrawCredit(Long userId,
                                                     TransactionCreditRequestDto transactionCreditRequestDto);
}
