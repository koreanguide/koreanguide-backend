package com.koreanguide.koreanguidebackend.domain.seoul.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParkInfoResponseDto {
    private String phoneNum;
    private String length;
    private String area;
    private String address;
    private String name;
    private String parkX;
    private String parkY;
    private List<ParkParkingInfoResponseDto> parkingData;
}
