package com.koreanguide.koreanguidebackend.domain.credit.controller;

import com.koreanguide.koreanguidebackend.config.security.JwtTokenProvider;
import com.koreanguide.koreanguidebackend.domain.credit.data.dto.request.BankAccountApplyRequestDto;
import com.koreanguide.koreanguidebackend.domain.credit.data.dto.request.TransactionCreditRequestDto;
import com.koreanguide.koreanguidebackend.domain.credit.data.dto.response.CreditHistoryResponseDto;
import com.koreanguide.koreanguidebackend.domain.credit.data.dto.response.CreditResponseDto;
import com.koreanguide.koreanguidebackend.domain.credit.data.dto.response.CreditReturningRequestResponseDto;
import com.koreanguide.koreanguidebackend.domain.credit.service.AccountService;
import com.koreanguide.koreanguidebackend.domain.credit.service.CreditService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(tags = {"Credit API"})
@RestController
@RequestMapping("/api/v1/credit")
public class CreditController {
    private final AccountService accountService;
    private final CreditService creditService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public CreditController(AccountService accountService, CreditService creditService,
                            JwtTokenProvider jwtTokenProvider) {
        this.accountService = accountService;
        this.creditService = creditService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @ApiOperation(value = "크레딧 환급 요청")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    @PostMapping("/refund")
    public ResponseEntity<?> requestReturningToAccount(HttpServletRequest request,
                                                                     @RequestParam Long amount) {
        return accountService.requestReturningToAccount(
                jwtTokenProvider.getUserIdByToken(request.getHeader("X-AUTH-TOKEN")), amount);
    }

    @ApiOperation(value = "크레딧 환급 요청 내역 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    @GetMapping("/refund")
    public ResponseEntity<List<CreditReturningRequestResponseDto>> getReturningHistory(HttpServletRequest request) {
        return accountService.getReturningHistory(
                jwtTokenProvider.getUserIdByToken(request.getHeader("X-AUTH-TOKEN")));
    }

    @ApiOperation(value = "최근 크레딧 환급 요청 정보 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    @GetMapping("/refund/recent")
    public ResponseEntity<?> getRecentReturningDay(HttpServletRequest request) {
        return accountService.getRecentReturningDay(
                jwtTokenProvider.getUserIdByToken(request.getHeader("X-AUTH-TOKEN")));
    }

    @ApiOperation(value = "은행 정보 등록")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    @PostMapping("/bank")
    public ResponseEntity<?> applyBankAccount(
            HttpServletRequest request,
            @RequestBody BankAccountApplyRequestDto bankAccountApplyRequestDto) {
        return accountService.applyBankAccount(jwtTokenProvider.getUserIdByToken(request.getHeader("X-AUTH-TOKEN")),
                bankAccountApplyRequestDto);
    }

    @ApiOperation(value = "크레딧 사용 내역 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    @GetMapping("/history")
    public ResponseEntity<List<CreditHistoryResponseDto>> getCreditHistory(HttpServletRequest request) {
        return creditService.getCreditHistory(jwtTokenProvider.getUserIdByToken(request.getHeader("X-AUTH-TOKEN")));
    }

    @ApiOperation(value = "은행 정보 삭제")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    @DeleteMapping("/bank")
    public ResponseEntity<?> removeBankAccount(HttpServletRequest request) {
        return accountService.removeBankAccount(
                jwtTokenProvider.getUserIdByToken(request.getHeader("X-AUTH-TOKEN")));
    }

    @ApiOperation(value = "은행 정보 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    @GetMapping("/bank")
    public ResponseEntity<?> getBankAccount(HttpServletRequest request) {
        return accountService.getBankAccount(jwtTokenProvider.getUserIdByToken(request.getHeader("X-AUTH-TOKEN")));
    }

    @ApiOperation(value = "보유 크레딧 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    @GetMapping("/")
    public ResponseEntity<CreditResponseDto> checkBalance(HttpServletRequest request) {
        return creditService.checkBalance(jwtTokenProvider.getUserIdByToken(request.getHeader("X-AUTH-TOKEN")));
    }

    @ApiOperation(value = "보유 크레딧 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    @PostMapping("/balance")
    public ResponseEntity<CreditResponseDto> checkBalanceTest(HttpServletRequest request) {
        return creditService.checkBalance(jwtTokenProvider.getUserIdByToken(request.getHeader("X-AUTH-TOKEN")));
    }

    @Deprecated
    @ApiOperation(value = "크레딧 출금")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    @PostMapping("/withdraw")
    public ResponseEntity<CreditResponseDto> withdrawCredit(
            HttpServletRequest request,
            @RequestBody TransactionCreditRequestDto transactionCreditRequestDto) {
        return creditService.withdrawCredit(jwtTokenProvider.getUserIdByToken(request.getHeader("X-AUTH-TOKEN")),
                transactionCreditRequestDto);
    }

    @Deprecated
    @ApiOperation(value = "크레딧 입금")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    @PostMapping("/deposit")
    public ResponseEntity<CreditResponseDto> depositCredit(
            HttpServletRequest request,
            @RequestBody TransactionCreditRequestDto transactionCreditRequestDto) {
        return creditService.depositCredit(jwtTokenProvider.getUserIdByToken(request.getHeader("X-AUTH-TOKEN")),
                transactionCreditRequestDto);
    }
}
