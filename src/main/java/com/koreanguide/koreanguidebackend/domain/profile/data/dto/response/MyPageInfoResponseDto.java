package com.koreanguide.koreanguidebackend.domain.profile.data.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyPageInfoResponseDto {
    private String email;
    private String password;
    private String name;
    private String phoneNum;
    private String registeredAt;
}
