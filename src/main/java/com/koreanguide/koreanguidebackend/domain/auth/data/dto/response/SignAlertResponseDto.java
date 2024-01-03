package com.koreanguide.koreanguidebackend.domain.auth.data.dto.response;

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
public class SignAlertResponseDto extends BaseResponseDto {
    private String en;
    private String ko;
}