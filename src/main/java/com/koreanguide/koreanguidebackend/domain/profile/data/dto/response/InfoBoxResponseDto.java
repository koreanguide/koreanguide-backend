package com.koreanguide.koreanguidebackend.domain.profile.data.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InfoBoxResponseDto {
    private String profileUrl;
    private String email;
    private String name;
    private Long credit;
}
