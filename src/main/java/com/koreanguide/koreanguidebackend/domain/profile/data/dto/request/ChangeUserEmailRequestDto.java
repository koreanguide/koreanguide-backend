package com.koreanguide.koreanguidebackend.domain.profile.data.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeUserEmailRequestDto {
    private String target;
    private String password;
    private String code;
}
