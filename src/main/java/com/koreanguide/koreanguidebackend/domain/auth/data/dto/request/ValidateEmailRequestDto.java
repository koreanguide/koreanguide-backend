package com.koreanguide.koreanguidebackend.domain.auth.data.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ValidateEmailRequestDto {
    private String email;
}
