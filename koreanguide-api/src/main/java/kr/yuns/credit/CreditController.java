package kr.yuns.credit;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import kr.yuns.JwtTokenProvider;
import kr.yuns.auth.data.dao.UserDao;
import kr.yuns.credit.data.dto.request.BankAccountApplyRequestDto;
import kr.yuns.credit.data.dto.request.TransactionCreditRequestDto;
import kr.yuns.credit.data.dto.response.CreditHistoryResponseDto;
import kr.yuns.credit.data.dto.response.CreditResponseDto;
import kr.yuns.credit.data.dto.response.CreditReturningRequestResponseDto;
import kr.yuns.credit.service.AccountService;
import kr.yuns.credit.service.CreditService;
import lombok.AllArgsConstructor;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/credit")
public class CreditController {
    private final AccountService accountService;
    private final CreditService creditService;
    private final JwtTokenProvider tokenProvider;
    private final UserDao userDao;

    public Long GET_USER_ID_BY_TOKEN(HttpServletRequest request) {
        return userDao.getUserId(tokenProvider.getUserEmail(request));
    }

    @PostMapping("/refund")
    public ResponseEntity<?> requestReturningToAccount(HttpServletRequest request, @RequestParam Long amount) {
        return accountService.requestReturningToAccount(GET_USER_ID_BY_TOKEN(request), amount);
    }

    @GetMapping("/refund")
    public ResponseEntity<List<CreditReturningRequestResponseDto>> getReturningHistory(HttpServletRequest request) {
        return accountService.getReturningHistory(GET_USER_ID_BY_TOKEN(request));
    }

    @GetMapping("/refund/recent")
    public ResponseEntity<?> getRecentReturningDay(HttpServletRequest request) {
        return accountService.getRecentReturningDay(GET_USER_ID_BY_TOKEN(request));
    }

    @PostMapping("/bank")
    public ResponseEntity<?> applyBankAccount(HttpServletRequest request, @RequestBody BankAccountApplyRequestDto bankAccountApplyRequestDto) {
        return accountService.applyBankAccount(GET_USER_ID_BY_TOKEN(request), bankAccountApplyRequestDto);
    }

    @GetMapping("/history")
    public ResponseEntity<List<CreditHistoryResponseDto>> getCreditHistory(HttpServletRequest request) {
        return creditService.getCreditHistory(GET_USER_ID_BY_TOKEN(request));
    }

    @DeleteMapping("/bank")
    public ResponseEntity<?> removeBankAccount(HttpServletRequest request) {
        return accountService.removeBankAccount(GET_USER_ID_BY_TOKEN(request));
    }

    @GetMapping("/bank")
    public ResponseEntity<?> getBankAccount(HttpServletRequest request) {
        return accountService.getBankAccount(GET_USER_ID_BY_TOKEN(request));
    }

    @GetMapping("/")
    public ResponseEntity<CreditResponseDto> checkBalance(HttpServletRequest request) {
        return creditService.checkBalance(GET_USER_ID_BY_TOKEN(request));
    }

    @PostMapping("/balance")
    public ResponseEntity<CreditResponseDto> checkBalanceTest(HttpServletRequest request) {
        return creditService.checkBalance(GET_USER_ID_BY_TOKEN(request));
    }

    @Deprecated
    @PostMapping("/withdraw")
    public ResponseEntity<CreditResponseDto> withdrawCredit(HttpServletRequest request, @RequestBody TransactionCreditRequestDto transactionCreditRequestDto) {
        return creditService.withdrawCredit(GET_USER_ID_BY_TOKEN(request), transactionCreditRequestDto);
    }

    @Deprecated
    @PostMapping("/deposit")
    public ResponseEntity<CreditResponseDto> depositCredit(HttpServletRequest request, @RequestBody TransactionCreditRequestDto transactionCreditRequestDto) {
        return creditService.depositCredit(GET_USER_ID_BY_TOKEN(request), transactionCreditRequestDto);
    }
}