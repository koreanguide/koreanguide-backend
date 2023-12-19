package com.koreanguide.koreanguidebackend.domain.credit.data.dto.request;

import com.koreanguide.koreanguidebackend.domain.credit.data.enums.AccountProvider;
import com.koreanguide.koreanguidebackend.domain.credit.data.enums.TransactionContent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionCreditRequestDto {
    private Long amount;
    private TransactionContent transactionContent = TransactionContent.WITHDRAW_TO_ACCOUNT;
}
