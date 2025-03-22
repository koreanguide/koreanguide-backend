package kr.yuns.seoul.data.dto.response;

import kr.yuns.seoul.data.enums.DustInfo;
import kr.yuns.seoul.data.enums.SkyInfo;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WeatherResponseDto {
    private String country;
    private String minTemp;
    private String maxTemp;
    private String nowTemp;
    private DustInfo ultrafineDust;
    private DustInfo findDust;
    private SkyInfo sky;
}