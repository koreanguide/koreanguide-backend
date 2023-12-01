package com.koreanguide.koreanguidebackend.domain.credit.data.dto.response;

import com.koreanguide.koreanguidebackend.domain.credit.data.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreditHistoryResponseDto {
    private Long amount;
    private String content;
    private LocalDateTime date;
    private String transactionType;
}
