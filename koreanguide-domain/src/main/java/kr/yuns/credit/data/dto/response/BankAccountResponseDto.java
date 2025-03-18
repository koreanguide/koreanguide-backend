package kr.yuns.credit.data.dto.response;

import kr.yuns.credit.data.enums.AccountProvider;
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