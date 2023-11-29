package com.koreanguide.koreanguidebackend.domain.credit.service;

import com.koreanguide.koreanguidebackend.domain.auth.data.dto.response.BaseResponseDto;
import com.koreanguide.koreanguidebackend.domain.credit.data.dto.request.BankAccountApplyRequestDto;
import org.springframework.http.ResponseEntity;

public interface AccountService {
    ResponseEntity<BaseResponseDto> applyBankAccount(Long userId,
                                                     BankAccountApplyRequestDto bankAccountApplyRequestDto);

    ResponseEntity<BaseResponseDto> removeBankAccount(Long userId);
}
