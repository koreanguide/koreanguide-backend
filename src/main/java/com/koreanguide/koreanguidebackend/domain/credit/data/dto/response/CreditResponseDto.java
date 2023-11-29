package com.koreanguide.koreanguidebackend.domain.credit.data.dto.response;

import com.koreanguide.koreanguidebackend.domain.credit.data.enums.AccountProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreditResponseDto {
    private boolean success;
    private String msg;
    private Long amount;
}
