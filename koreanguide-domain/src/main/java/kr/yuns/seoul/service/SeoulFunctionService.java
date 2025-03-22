package kr.yuns.seoul.service;

import java.io.IOException;
import java.net.URISyntaxException;

import kr.yuns.auth.data.enums.SeoulCountry;
import kr.yuns.seoul.data.entity.DustData;
import kr.yuns.seoul.data.entity.SeoulCoordinate;
import kr.yuns.seoul.data.entity.WeatherData;
import kr.yuns.seoul.data.enums.DustInfo;

public interface SeoulFunctionService {
    String extractFromURL(String url);
    String getKaraokeApiUrl(SeoulCountry country);
    SeoulCoordinate getSeoulCoordinate(SeoulCountry seoulCountry);
    String getSeoulCountryName(SeoulCountry seoulCountry);
    DustInfo convertFineDustData(int data);
    DustInfo convertUltraFineDustData(int data);
    String convertKakaoMapUrl(String LAT, String LONG);
    DustData callDustData(SeoulCountry seoulCountry) throws URISyntaxException;
    WeatherData callWeatherData(SeoulCountry seoulCountry) throws IOException;
}
