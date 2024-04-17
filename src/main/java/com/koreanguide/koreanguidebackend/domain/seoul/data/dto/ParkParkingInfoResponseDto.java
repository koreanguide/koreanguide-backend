package com.koreanguide.koreanguidebackend.domain.seoul.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParkParkingInfoResponseDto {
    private String name;
    private String address;
    private String available;
    private String week;
    private String weekend;
    private String startFee;
    private String additionalFee;
    private String phoneNum;
}
