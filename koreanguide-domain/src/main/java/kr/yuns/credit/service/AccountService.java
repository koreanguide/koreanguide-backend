package kr.yuns.credit.service;

import org.springframework.http.ResponseEntity;

import kr.yuns.credit.data.dto.request.BankAccountApplyRequestDto;
import kr.yuns.credit.data.dto.response.CreditReturningRequestResponseDto;

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