package com.koreanguide.koreanguidebackend.domain.auth.data.dto.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Builder
public class SignAlertResponseDto {
    private String en;
    private String ko;
}