package com.koreanguide.koreanguidebackend.domain.seoul.data.entity;

import com.koreanguide.koreanguidebackend.domain.seoul.data.enums.SkyInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherData {
    private String minTemp;
    private String maxTemp;
    private String nowTemp;
    private SkyInfo sky;
}
