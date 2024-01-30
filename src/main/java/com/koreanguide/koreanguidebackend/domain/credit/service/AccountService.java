package com.koreanguide.koreanguidebackend.domain.credit.service;

import com.koreanguide.koreanguidebackend.domain.auth.data.dto.response.BaseResponseDto;
import com.koreanguide.koreanguidebackend.domain.credit.data.dto.request.BankAccountApplyRequestDto;
import com.koreanguide.koreanguidebackend.domain.credit.data.dto.response.CreditReturningRequestResponseDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AccountService {
    ResponseEntity<BaseResponseDto> requestReturningToAccount(Long userId, Long amount);

    ResponseEntity<List<CreditReturningRequestResponseDto>> getReturningHistory(Long userId);

    ResponseEntity<?> getRecentReturningDay(Long userId);

    ResponseEntity<?> getBankAccount(Long userId);

    ResponseEntity<BaseResponseDto> applyBankAccount(Long userId,
                                                     BankAccountApplyRequestDto bankAccountApplyRequestDto);

    ResponseEntity<BaseResponseDto> removeBankAccount(Long userId);
}
