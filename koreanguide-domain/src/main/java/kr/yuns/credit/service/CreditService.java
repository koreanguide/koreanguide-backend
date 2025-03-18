package kr.yuns.credit.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import kr.yuns.credit.data.dto.request.TransactionCreditRequestDto;
import kr.yuns.credit.data.dto.response.CreditHistoryResponseDto;
import kr.yuns.credit.data.dto.response.CreditResponseDto;
import kr.yuns.credit.data.enums.TransactionContent;

public interface CreditService {
    ResponseEntity<CreditResponseDto> checkBalance(Long userId);
    ResponseEntity<List<CreditHistoryResponseDto>> getCreditHistory(Long userId);
    void depositCreditToUser(Long userId, Long amount, TransactionContent transactionContent);
    ResponseEntity<CreditResponseDto> depositCredit(Long userId,
                                                    TransactionCreditRequestDto transactionCreditRequestDto);
    ResponseEntity<CreditResponseDto> withdrawCredit(Long userId,
                                                     TransactionCreditRequestDto transactionCreditRequestDto);
}