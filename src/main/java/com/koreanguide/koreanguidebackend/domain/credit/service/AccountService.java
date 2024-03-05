package com.koreanguide.koreanguidebackend.domain.credit.service;

import com.koreanguide.koreanguidebackend.domain.credit.data.dto.request.BankAccountApplyRequestDto;
import com.koreanguide.koreanguidebackend.domain.credit.data.dto.response.CreditReturningRequestResponseDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AccountService {
    ResponseEntity<?> requestReturningToAccount(Long userId, Long amount);
    ResponseEntity<List<CreditReturningRequestResponseDto>> getReturningHistory(Long userId);
    ResponseEntity<?> getRecentReturningDay(Long userId);
    ResponseEntity<?> getBankAccount(Long userId);
    ResponseEntity<?> applyBankAccount(Long userId,
                                                     BankAccountApplyRequestDto bankAccountApplyRequestDto);
    ResponseEntity<?> removeBankAccount(Long userId);
}
