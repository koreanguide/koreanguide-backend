package com.koreanguide.koreanguidebackend.domain.credit.controller;

import com.koreanguide.koreanguidebackend.domain.auth.data.dto.response.BaseResponseDto;
import com.koreanguide.koreanguidebackend.domain.credit.data.dto.request.BankAccountApplyRequestDto;
import com.koreanguide.koreanguidebackend.domain.credit.data.dto.request.TransactionCreditRequestDto;
import com.koreanguide.koreanguidebackend.domain.credit.data.dto.response.CreditHistoryResponseDto;
import com.koreanguide.koreanguidebackend.domain.credit.data.dto.response.CreditResponseDto;
import com.koreanguide.koreanguidebackend.domain.credit.data.dto.response.CreditReturningRequestResponseDto;
import com.koreanguide.koreanguidebackend.domain.credit.service.AccountService;
import com.koreanguide.koreanguidebackend.domain.credit.service.CreditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/refund")
    public ResponseEntity<BaseResponseDto> requestReturningToAccount(@RequestParam Long userId, Long amount) {
        return accountService.requestReturningToAccount(userId, amount);
    }

    @GetMapping("/refund")
    public ResponseEntity<List<CreditReturningRequestResponseDto>> getReturningHistory(@RequestParam Long userId) {
        return accountService.getReturningHistory(userId);
    }

    @PostMapping("/bank")
    public ResponseEntity<BaseResponseDto> applyBankAccount(
            @RequestParam Long userId,
            @RequestBody BankAccountApplyRequestDto bankAccountApplyRequestDto) {
        return accountService.applyBankAccount(userId, bankAccountApplyRequestDto);
    }

    @GetMapping("/history")
    public ResponseEntity<List<CreditHistoryResponseDto>> getCreditHistory(@RequestParam Long userId) {
        return creditService.getCreditHistory(userId);
    }

    @DeleteMapping("/bank")
    public ResponseEntity<BaseResponseDto> removeBankAccount(@RequestParam Long userId) {
        return accountService.removeBankAccount(userId);
    }

    @GetMapping("/")
    public ResponseEntity<CreditResponseDto> checkBalance(@RequestParam Long userId) {
        return creditService.checkBalance(userId);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<CreditResponseDto> withdrawCredit(
            @RequestBody TransactionCreditRequestDto transactionCreditRequestDto) {
        return creditService.withdrawCredit(transactionCreditRequestDto);
    }

    @PostMapping("/deposit")
    public ResponseEntity<CreditResponseDto> depositCredit(
            @RequestBody TransactionCreditRequestDto transactionCreditRequestDto) {
        return creditService.depositCredit(transactionCreditRequestDto);
    }
}
