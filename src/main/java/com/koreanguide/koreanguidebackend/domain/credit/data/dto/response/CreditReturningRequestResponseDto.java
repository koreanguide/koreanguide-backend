package com.koreanguide.koreanguidebackend.domain.credit.data.dto.response;

import com.koreanguide.koreanguidebackend.domain.credit.data.enums.ReturningStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreditReturningRequestResponseDto {
    private Long amount;
    private ReturningStatus returningStatus;
    private LocalDateTime requestDate;
    private LocalDateTime updateDate;
}
