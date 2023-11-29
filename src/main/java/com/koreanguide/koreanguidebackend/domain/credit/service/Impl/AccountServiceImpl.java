package com.koreanguide.koreanguidebackend.domain.credit.service.Impl;

import com.koreanguide.koreanguidebackend.domain.auth.data.dto.response.BaseResponseDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.auth.data.repository.UserRepository;
import com.koreanguide.koreanguidebackend.domain.credit.data.dto.request.BankAccountApplyRequestDto;
import com.koreanguide.koreanguidebackend.domain.credit.data.entity.BankAccounts;
import com.koreanguide.koreanguidebackend.domain.credit.data.repository.BankAccountsRepository;
import com.koreanguide.koreanguidebackend.domain.credit.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {
    private final BankAccountsRepository bankAccountsRepository;
    private final UserRepository userRepository;

    @Autowired
    public AccountServiceImpl(BankAccountsRepository bankAccountsRepository, UserRepository userRepository) {
        this.bankAccountsRepository = bankAccountsRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ResponseEntity<BaseResponseDto> applyBankAccount(Long userId,
                                                            BankAccountApplyRequestDto bankAccountApplyRequestDto) {
        Optional<User> user = userRepository.findById(userId);

        try {
            if(user.isEmpty()) {
                throw new RuntimeException("사용자를 찾을 수 없음");
            }

            Optional<BankAccounts> bankAccounts = bankAccountsRepository.findBankAccountsByUser(user.get());

            if(bankAccounts.isPresent()) {
                bankAccountsRepository.save(BankAccounts.builder()
                        .accountProvider(bankAccountApplyRequestDto.getBankAccountProvider())
                        .accountNumber(bankAccountApplyRequestDto.getBankAccountNumber())
                        .accountHolderName(bankAccountApplyRequestDto.getName())
                        .verified(true)
                        .user(user.get())
                        .appliedAt(LocalDateTime.now())
                        .build());
            } else {
                BankAccounts foundBankAccounts = bankAccounts.get();
                foundBankAccounts.setAccountProvider(bankAccountApplyRequestDto.getBankAccountProvider());
                foundBankAccounts.setAccountHolderName(bankAccountApplyRequestDto.getName());
                foundBankAccounts.setAccountNumber(bankAccountApplyRequestDto.getBankAccountNumber());
                foundBankAccounts.setAppliedAt(LocalDateTime.now());

                bankAccountsRepository.save(foundBankAccounts);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseDto.builder()
                        .success(true)
                        .msg("계좌 등록이 완료되었습니다.")
                .build());
    }

    @Override
    public ResponseEntity<BaseResponseDto> removeBankAccount(Long userId) {
        try {
            Optional<User> user = userRepository.findById(userId);

            if(user.isEmpty()) {
                throw new RuntimeException("사용자를 찾을 수 없음");
            }

            Optional<BankAccounts> bankAccounts = bankAccountsRepository.findBankAccountsByUser(user.get());

            if(bankAccounts.isEmpty()) {
                throw new RuntimeException("등록된 계좌가 없음");
            } else {
                bankAccountsRepository.delete(bankAccounts.get());
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseDto.builder()
                        .success(true)
                        .msg("등록된 계좌 삭제가 완료되었습니다.")
                .build());
    }
}
