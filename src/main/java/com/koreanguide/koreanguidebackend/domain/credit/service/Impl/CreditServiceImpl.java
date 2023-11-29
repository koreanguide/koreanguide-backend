package com.koreanguide.koreanguidebackend.domain.credit.service.Impl;

import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.auth.data.repository.UserRepository;
import com.koreanguide.koreanguidebackend.domain.credit.data.dto.response.CreditResponseDto;
import com.koreanguide.koreanguidebackend.domain.credit.data.entity.Credit;
import com.koreanguide.koreanguidebackend.domain.credit.data.repository.CreditRepository;
import com.koreanguide.koreanguidebackend.domain.credit.service.CreditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CreditServiceImpl implements CreditService {
    private final CreditRepository creditRepository;
    private final UserRepository userRepository;

    @Autowired
    public CreditServiceImpl(CreditRepository creditRepository, UserRepository userRepository) {
        this.creditRepository = creditRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ResponseEntity<CreditResponseDto> checkBalance(Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if(user.isEmpty()) {
            throw new RuntimeException("사용자를 찾을 수 없음");
        }

        Optional<Credit> credit = creditRepository.findByUser(user.get());

        if(credit.isEmpty()) {
            creditRepository.save(Credit.builder()
                    .amount(0L)
                    .recentUsed(LocalDateTime.now())
                    .user(user.get())
                    .build());

            return ResponseEntity.status(HttpStatus.OK).body(CreditResponseDto.builder()
                            .success(true)
                            .msg("정상 처리")
                            .amount(0L)
                    .build());
        }

        return ResponseEntity.status(HttpStatus.OK).body(CreditResponseDto.builder()
                        .success(true)
                        .msg("정상 처리")
                        .amount(credit.get().getAmount())
                .build());
    }

    @Override
    public ResponseEntity<CreditResponseDto> depositCredit(Long userId, Long amount) {
        Optional<User> user = userRepository.findById(userId);

        if(user.isEmpty()) {
            throw new RuntimeException("사용자를 찾을 수 없음");
        }

        Optional<Credit> credit = creditRepository.findByUser(user.get());

        if(credit.isEmpty()) {
            creditRepository.save(Credit.builder()
                            .amount(amount)
                            .recentUsed(LocalDateTime.now())
                    .build());
        } else {
            Credit foundCredit = credit.get();
            foundCredit.setAmount(foundCredit.getAmount() + amount);
            foundCredit.setRecentUsed(LocalDateTime.now());

            creditRepository.save(foundCredit);
        }

        return ResponseEntity.status(HttpStatus.OK).body(CreditResponseDto.builder()
                        .success(true)
                        .msg("정상 처리")
                        .amount(creditRepository.getByUser(user.get()).getAmount())
                .build());
    }

    @Override
    public ResponseEntity<CreditResponseDto> withdrawCredit(Long userId, Long amount) {
        Optional<User> user = userRepository.findById(userId);

        if(user.isEmpty()) {
            throw new RuntimeException("사용자를 찾을 수 없음");
        }

        Optional<Credit> credit = creditRepository.findByUser(user.get());

        if(credit.isEmpty()) {
            creditRepository.save(Credit.builder()
                    .amount(0L)
                    .recentUsed(LocalDateTime.now())
                    .build());

            return ResponseEntity.status(HttpStatus.OK).body(CreditResponseDto.builder()
                            .success(false)
                            .msg("잔액 부족")
                            .amount(0L)
                    .build());
        } else {
            Credit foundCredit = credit.get();

            if(foundCredit.getAmount() - amount < 0) {
                return ResponseEntity.status(HttpStatus.OK).body(CreditResponseDto.builder()
                                .success(false)
                                .msg("잔액 부족")
                                .amount(foundCredit.getAmount())
                        .build());
            } else {
                foundCredit.setAmount(foundCredit.getAmount() - amount);
                foundCredit.setRecentUsed(LocalDateTime.now());

                creditRepository.save(foundCredit);

                return ResponseEntity.status(HttpStatus.OK).body(CreditResponseDto.builder()
                        .success(true)
                        .msg("정상 처리")
                        .amount(foundCredit.getAmount() - amount)
                        .build());
            }
        }
    }
}
