package com.koreanguide.koreanguidebackend.domain.profile.data.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangePasswordRequestDto {
    private String password;
    private String newPassword;
}
