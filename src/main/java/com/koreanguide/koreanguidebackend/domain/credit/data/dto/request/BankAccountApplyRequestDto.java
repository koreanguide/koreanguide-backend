package com.koreanguide.koreanguidebackend.domain.credit.data.dto.request;

import com.koreanguide.koreanguidebackend.domain.credit.data.enums.AccountProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankAccountApplyRequestDto {
    private AccountProvider bankAccountProvider = AccountProvider.SHINHAN;
    private String bankAccountNumber;
    private String name;
}
