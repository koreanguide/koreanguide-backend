package com.koreanguide.koreanguidebackend.domain.seoul.data.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AttractionsResponseDto {
    private String tag;
    private double latitude;
    private double longitude;
    private String title;
    private String address;
}
