package com.koreanguide.koreanguidebackend.domain.seoul.data.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KaraokeResponseDto {
    private String phoneNum;
    private String name;
    private String address;
}
