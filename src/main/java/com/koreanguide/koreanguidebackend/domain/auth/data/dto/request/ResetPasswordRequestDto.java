package com.koreanguide.koreanguidebackend.domain.auth.data.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ResetPasswordRequestDto {
    private String email;
    private String validateKey;
    private String password;
}
