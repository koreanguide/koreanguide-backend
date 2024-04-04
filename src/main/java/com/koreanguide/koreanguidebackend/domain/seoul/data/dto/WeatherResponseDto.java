package com.koreanguide.koreanguidebackend.domain.seoul.data.dto;

import com.koreanguide.koreanguidebackend.domain.seoul.data.enums.DustInfo;
import com.koreanguide.koreanguidebackend.domain.seoul.data.enums.SkyInfo;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WeatherResponseDto {
    private String minTemp;
    private String maxTemp;
    private String nowTemp;
    private DustInfo ultrafineDust;
    private DustInfo findDust;
    private SkyInfo sky;
}
