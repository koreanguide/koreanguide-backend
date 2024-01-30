package com.koreanguide.koreanguidebackend.domain.profile.data.dto.response;

import com.koreanguide.koreanguidebackend.domain.credit.data.enums.AccountProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyPageResponseDto {
    private String name;
    private String nickName;
    private String phoneNum;
    private String email;
    private String password;
    private String accountInfo;
    private String blocked;
    private boolean isEnable;
}
