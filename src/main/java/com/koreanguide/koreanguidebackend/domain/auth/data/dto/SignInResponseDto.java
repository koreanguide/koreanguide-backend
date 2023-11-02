package com.koreanguide.koreanguidebackend.domain.auth.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@SuperBuilder
public class SignInResponseDto extends BaseResponseDto {
    private String accessToken;
    private String refreshToken;
    private String email;
}