package com.koreanguide.koreanguidebackend.domain.seoul.data.dto;

import com.koreanguide.koreanguidebackend.domain.seoul.data.enums.RiverPark;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ParkResponseDto {
    private List<ParkListResponseDto> data;
    private List<ParkListResponseDto> recommend;
}
