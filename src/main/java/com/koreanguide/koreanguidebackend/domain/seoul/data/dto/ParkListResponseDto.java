package com.koreanguide.koreanguidebackend.domain.seoul.data.dto;

import com.koreanguide.koreanguidebackend.domain.seoul.data.enums.RiverPark;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParkListResponseDto {
    private String name;
    private String address;
    private RiverPark riverPark;
}
