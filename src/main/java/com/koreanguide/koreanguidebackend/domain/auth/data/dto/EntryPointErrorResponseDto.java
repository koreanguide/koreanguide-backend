package com.koreanguide.koreanguidebackend.domain.auth.data.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class EntryPointErrorResponseDto {
    private String msg;
}
