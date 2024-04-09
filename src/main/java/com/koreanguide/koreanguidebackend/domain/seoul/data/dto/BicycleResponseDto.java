package com.koreanguide.koreanguidebackend.domain.seoul.data.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BicycleResponseDto {
    private String code;
    private String count;
    private String name;
    private String address;
    private String kakaoMapUrl;
}
