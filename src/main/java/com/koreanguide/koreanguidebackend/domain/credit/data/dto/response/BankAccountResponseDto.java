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
public class BankAccountResponseDto {
    private AccountProvider accountProvider;
    private String accountNumber;
    private String accountHolderName;
}
