package com.koreanguide.koreanguidebackend.domain.credit.controller;

import com.koreanguide.koreanguidebackend.domain.auth.data.dto.response.BaseResponseDto;
import com.koreanguide.koreanguidebackend.domain.credit.data.dto.request.BankAccountApplyRequestDto;
import com.koreanguide.koreanguidebackend.domain.credit.data.dto.response.CreditResponseDto;
import com.koreanguide.koreanguidebackend.domain.credit.service.AccountService;
import com.koreanguide.koreanguidebackend.domain.credit.service.CreditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/credit")
public class CreditController {
    private final AccountService accountService;
    private final CreditService creditService;

    @Autowired
    public CreditController(AccountService accountService, CreditService creditService) {
        this.accountService = accountService;
        this.creditService = creditService;
    }

    @PostMapping("/bank")
    public ResponseEntity<BaseResponseDto> applyBankAccount(
            Long userId, BankAccountApplyRequestDto bankAccountApplyRequestDto) {
        return accountService.applyBankAccount(userId, bankAccountApplyRequestDto);
    }

    @DeleteMapping("/bank")
    public ResponseEntity<BaseResponseDto> removeBankAccount(Long userId) {
        return accountService.removeBankAccount(userId);
    }

    @GetMapping("/")
    public ResponseEntity<CreditResponseDto> checkBalance(Long userId) {
        return creditService.checkBalance(userId);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<CreditResponseDto> withdrawCredit(Long userId, Long amount) {
        return creditService.withdrawCredit(userId, amount);
    }

    @PostMapping("/deposit")
    public ResponseEntity<CreditResponseDto> depositCredit(Long userId, Long amount) {
        return creditService.depositCredit(userId, amount);
    }
}
