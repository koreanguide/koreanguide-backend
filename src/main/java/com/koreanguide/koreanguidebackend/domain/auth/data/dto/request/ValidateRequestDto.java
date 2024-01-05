package com.koreanguide.koreanguidebackend.domain.auth.data.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ValidateRequestDto {
    private String email;
    private String key;
}
