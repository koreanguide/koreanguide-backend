package com.koreanguide.koreanguidebackend.domain.auth.data.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class TokenResponseDto {
    private String accessToken;
}
