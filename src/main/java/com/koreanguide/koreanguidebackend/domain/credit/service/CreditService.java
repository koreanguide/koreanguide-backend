package com.koreanguide.koreanguidebackend.domain.credit.service;

import com.koreanguide.koreanguidebackend.domain.credit.data.dto.request.TransactionCreditRequestDto;
import com.koreanguide.koreanguidebackend.domain.credit.data.dto.response.CreditResponseDto;
import org.springframework.http.ResponseEntity;

public interface CreditService {
    ResponseEntity<CreditResponseDto> checkBalance(Long userId);

    ResponseEntity<CreditResponseDto> depositCredit(TransactionCreditRequestDto transactionCreditRequestDto);

    ResponseEntity<CreditResponseDto> withdrawCredit(TransactionCreditRequestDto transactionCreditRequestDto);
}
