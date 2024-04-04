package com.koreanguide.koreanguidebackend.domain.seoul.service.Impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.koreanguide.koreanguidebackend.domain.auth.data.dao.UserDao;
import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.auth.data.enums.SeoulCountry;
import com.koreanguide.koreanguidebackend.domain.seoul.data.dto.WeatherResponseDto;
import com.koreanguide.koreanguidebackend.domain.seoul.data.enums.DustInfo;
import com.koreanguide.koreanguidebackend.domain.seoul.data.dto.ShopResponseDto;
import com.koreanguide.koreanguidebackend.domain.seoul.data.enums.SkyInfo;
import com.koreanguide.koreanguidebackend.domain.seoul.data.entity.DustData;
import com.koreanguide.koreanguidebackend.domain.seoul.data.entity.SeoulCoordinate;
import com.koreanguide.koreanguidebackend.domain.seoul.data.entity.WeatherData;
import com.koreanguide.koreanguidebackend.domain.seoul.service.SeoulService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class SeoulServiceImpl implements SeoulService {
    private final UserDao userDao;
    private String SEOUL_API_KEY;
    private String SEOUL_SHOPPING_CENTER_LIST_API;
    private String SEOUL_DUST_INFO_API;

    public SeoulServiceImpl(UserDao userDao, @Value("${seoul.api.key}") String SEOUL_API_KEY) {
        this.userDao = userDao;
        this.SEOUL_API_KEY = SEOUL_API_KEY;
        this.SEOUL_SHOPPING_CENTER_LIST_API = "http://openapi.seoul.go.kr:8088/" + this.SEOUL_API_KEY + "/json/SebcShoppingCenterKor/1/1000/";
        this.SEOUL_DUST_INFO_API = "http://openAPI.seoul.go.kr:8088/" + this.SEOUL_API_KEY + "/json/ListAirQualityByDistrictService/1/5/";
    }

    public SeoulCoordinate getSeoulCoordinate(SeoulCountry seoulCountry) {
        String nx = "61";
        String ny = "126";

        switch (seoulCountry) {
            case GANGNAM:
                nx = "61";
                ny = "126";
                break;
            case GANGDONG:
            case SONGPA:
            case GWANGJIN:
                nx = "62";
                ny = "126";
                break;
            case GWANAK:
            case DONGJAK:
                nx = "59";
                ny = "125";
                break;
            case GANGSEO:
            case YANGCHEON:
            case YONGDENGPO:
                nx = "58";
                ny = "126";
                break;
            case GANGBUK:
                nx = "61";
                ny = "128";
                break;
            case GURO:
                nx = "58";
                ny = "125";
                break;
            case GEUMCHEON:
                nx = "59";
                ny = "124";
                break;
            case NOWON:
            case DOBONG:
                nx = "61";
                ny = "129";
                break;
            case DONGDAEMUN:
            case SEONGDONG:
            case SEONGBUK:
                nx = "61";
                ny = "127";
                break;
            case MAPO:
            case SEODAEMUN:
            case EUNPYEONG:
                nx = "59";
                ny = "127";
                break;
            case SEOCHO:
                nx = "61";
                ny = "125";
                break;
            case YONGSAN:
                nx = "60";
                ny = "126";
                break;
            case JONGNO:
            case JUNG:
                nx = "60";
                ny = "127";
                break;
            case JUNGNANG:
                nx = "62";
                ny = "128";
                break;
        }

        return SeoulCoordinate.builder()
                    .nx(nx)
                    .ny(ny)
                .build();
    }

    public String CONVERT_COUNTRY_TO_CODE(SeoulCountry seoulCountry) {
        String country = "111261";

        switch (seoulCountry) {
            case GANGNAM:
                country = "111261";
                break;
            case GANGDONG:
                country = "111274";
                break;
            case GWANAK:
                country = "111251";
                break;
            case GANGSEO:
                country = "111212";
                break;
            case GANGBUK:
                country = "111291";
                break;
            case GWANGJIN:
                country = "111141";
                break;
            case GURO:
                country = "111221";
                break;
            case GEUMCHEON:
                country = "111281";
                break;
            case NOWON:
                country = "111311";
                break;
            case DOBONG:
                country = "111171";
                break;
            case DONGDAEMUN:
                country = "111152";
                break;
            case DONGJAK:
                country = "111241";
                break;
            case MAPO:
                country = "111201";
                break;
            case SEODAEMUN:
                country = "111191";
                break;
            case SEOCHO:
                country = "111262";
                break;
            case SEONGDONG:
                country = "111142";
                break;
            case SEONGBUK:
                country = "111161";
                break;
            case SONGPA:
                country = "111273";
                break;
            case YANGCHEON:
                country = "111301";
                break;
            case YONGDENGPO:
                country = "111231";
                break;
            case YONGSAN:
                country = "111131";
                break;
            case EUNPYEONG:
                country = "111181";
                break;
            case JONGNO:
                country = "111123";
                break;
            case JUNG:
                country = "111121";
                break;
            case JUNGNANG:
                country = "111151";
                break;
        }

        return country;
    }

    public String getSeoulCountryName(SeoulCountry seoulCountry) {
        String country = "강남구";

        switch (seoulCountry) {
            case GANGNAM:
                country = "강남구";
                break;
            case GANGDONG:
                country = "강동구";
                break;
            case GWANAK:
                country = "관악구";
                break;
            case GANGSEO:
                country = "강서구";
                break;
            case GANGBUK:
                country = "강북구";
                break;
            case GWANGJIN:
                country = "광진구";
                break;
            case GURO:
                country = "구로구";
                break;
            case GEUMCHEON:
                country = "금천구";
                break;
            case NOWON:
                country = "노원구";
                break;
            case DOBONG:
                country = "도봉구";
                break;
            case DONGDAEMUN:
                country = "동대문구";
                break;
            case DONGJAK:
                country = "동작구";
                break;
            case MAPO:
                country = "마포구";
                break;
            case SEODAEMUN:
                country = "서대문구";
                break;
            case SEOCHO:
                country = "서초구";
                break;
            case SEONGDONG:
                country = "성동구";
                break;
            case SEONGBUK:
                country = "성북구";
                break;
            case SONGPA:
                country = "송파구";
                break;
            case YANGCHEON:
                country = "양천구";
                break;
            case YONGDENGPO:
                country = "영등포구";
                break;
            case YONGSAN:
                country = "용산구";
                break;
            case EUNPYEONG:
                country = "은평구";
                break;
            case JONGNO:
                country = "종로구";
                break;
            case JUNG:
                country = "중구";
                break;
            case JUNGNANG:
                country = "중량구";
                break;
        }

        return country;
    }

    public DustInfo CONVERT_DUST_DATA(int data) {
        if(data >= 0 && data <= 15) {
            return DustInfo.GOOD;
        } else if (data > 15 && data <= 35) {
            return DustInfo.NORMAL;
        } else if (data > 35 && data <= 75) {
            return DustInfo.BAD;
        } else {
            return DustInfo.WORST;
        }
    }

    public DustData CALL_DUST_DATA(SeoulCountry seoulCountry) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            String COUNTRY_CODE = CONVERT_COUNTRY_TO_CODE(seoulCountry);

            JsonNode root = mapper.readTree(new URL(SEOUL_DUST_INFO_API + COUNTRY_CODE + "/"));
            JsonNode rows = root.path("ListAirQualityByDistrictService").path("row");

            int PM_10_DATA = Integer.parseInt("0");
            int PM_25_DATA = Integer.parseInt("0");

            for (JsonNode row : rows) {
                PM_10_DATA = Integer.parseInt(row.get("PM10").asText());
                PM_25_DATA = Integer.parseInt(row.get("PM25").asText());
            }

            return DustData.builder()
                        .fineDust(CONVERT_DUST_DATA(PM_10_DATA))
                        .ultraFineDust(CONVERT_DUST_DATA(PM_25_DATA))
                    .build();

        } catch (IOException e) {
            return DustData.builder()
                        .fineDust(DustInfo.UNKNOWN)
                        .ultraFineDust(DustInfo.UNKNOWN)
                    .build();
        }
    }

    public WeatherData CALL_WEATHER_DATA(SeoulCountry seoulCountry) throws IOException {
        LocalDateTime CURRENT_TIME = LocalDateTime.now();
        LocalDateTime ADJUSTED_TIME = CURRENT_TIME.truncatedTo(ChronoUnit.HOURS);

        SeoulCoordinate seoulCoordinate = getSeoulCoordinate(seoulCountry);

        int hour = ADJUSTED_TIME.getHour();
        String base_date;
        String base_time;

        if (hour < 2) {
            ADJUSTED_TIME = ADJUSTED_TIME.minusDays(1);
            base_date = ADJUSTED_TIME.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            base_time = "2300";
        } else {
            base_date = ADJUSTED_TIME.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            int adjustedHour = hour - (hour % 3 == 0 ? 3 : hour % 3);
            if (adjustedHour == 0) {
                ADJUSTED_TIME = ADJUSTED_TIME.minusDays(1);
                base_date = ADJUSTED_TIME.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                base_time = "2300";
            } else {
                base_time = String.format("%02d00", adjustedHour);
            }
        }

        String urlBuilder = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst" + "?" + URLEncoder.encode("serviceKey", StandardCharsets.UTF_8) + "=z2I9YCbpCq1a5T%2BxmhqssSL3zWq2IVBTYusxgVlwvOR3kwy9vgokbtJ8xRuArqGZ27DClJUkfIGdP9KGZvH%2FFw%3D%3D" +
                "&" + URLEncoder.encode("pageNo", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("1", StandardCharsets.UTF_8) +
                "&" + URLEncoder.encode("numOfRows", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("1000", StandardCharsets.UTF_8) +
                "&" + URLEncoder.encode("dataType", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("JSON", StandardCharsets.UTF_8) +
                "&" + URLEncoder.encode("base_date", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(base_date, StandardCharsets.UTF_8) +
                "&" + URLEncoder.encode("base_time", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(base_time, StandardCharsets.UTF_8) +
                "&" + URLEncoder.encode("nx", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(seoulCoordinate.getNx(), StandardCharsets.UTF_8) +
                "&" + URLEncoder.encode("ny", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(seoulCoordinate.getNy(), StandardCharsets.UTF_8);
        URL url = new URL(urlBuilder);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(String.valueOf(sb));
        JsonNode rows = root.path("response").path("body").path("items").path("item");

        WeatherData weatherData = new WeatherData();

        for (JsonNode row : rows) {
            String category = row.path("category").asText();

            // 최고 기온
            if ("TMX".equals(category)) {
                String TMX_VALUE = row.path("fcstValue").asText();
                weatherData.setMaxTemp(TMX_VALUE);
            }

            // 최저 기온
            if ("TMN".equals(category)) {
                String TMN_VALUE = row.path("fcstValue").asText();
                weatherData.setMinTemp(TMN_VALUE);
            }

            // 현재 기온
            if ("TMP".equals(category)) {
                String TMP_VALUE = row.path("fcstValue").asText();
                weatherData.setNowTemp(TMP_VALUE);
            }

            // 하늘 상태
            if ("SKY".equals(category)) {
                String PTY_VALUE = row.path("fcstValue").asText();
                switch (PTY_VALUE) {
                    case "1":
                        weatherData.setSky(SkyInfo.RAIN);
                        break;
                    case "2":
                        weatherData.setSky(SkyInfo.RAIN_AND_SNOW);
                        break;
                    case "3":
                        weatherData.setSky(SkyInfo.SNOW);
                        break;
                    case "5":
                        weatherData.setSky(SkyInfo.RAINDROP);
                        break;
                    case "6":
                        weatherData.setSky(SkyInfo.RAINDROP_AND_SNOWFALL);
                        break;
                    case "7":
                        weatherData.setSky(SkyInfo.SNOWFALL);
                        break;
                    default:
                        weatherData.setSky(SkyInfo.NORMAL);
                        break;
                }
            }
        }
        rd.close();
        conn.disconnect();

        return weatherData;
    }

    @Override
    public ResponseEntity<?> getSeoulWeather(Long userId) throws IOException {
        User user = userDao.getUserEntity(userId);
        WeatherData weatherData = CALL_WEATHER_DATA(user.getCountry());
        DustData dustData = CALL_DUST_DATA(user.getCountry());

        return ResponseEntity.status(HttpStatus.OK).body(WeatherResponseDto.builder()
                        .minTemp(weatherData.getMinTemp())
                        .maxTemp(weatherData.getMaxTemp())
                        .nowTemp(weatherData.getNowTemp())
                        .sky(weatherData.getSky())
                        .findDust(dustData.getFineDust())
                        .ultrafineDust(dustData.getUltraFineDust())
                .build());
    }

    @Override
    public ResponseEntity<?> getSeoulShopList(SeoulCountry seoulCountry) {
        try {
            List<ShopResponseDto> shopResponseDtoList = new ArrayList<>();
            ObjectMapper mapper = new ObjectMapper();

            JsonNode root = mapper.readTree(new URL(SEOUL_SHOPPING_CENTER_LIST_API));
            JsonNode rows = root.path("SebcShoppingCenterKor").path("row");

            for (JsonNode row : rows) {
                String hKorGu = row.path("H_KOR_GU").asText();
                if (getSeoulCountryName(seoulCountry).equals(hKorGu)) {
                    shopResponseDtoList.add(ShopResponseDto.builder()
                                    .nameKor(row.get("NAME_KOR").asText())
                                    .cate2Name(row.get("CATE2_NAME").asText())
                                    .cate3Name(row.get("CATE3_NAME").asText())
                                    .address(
                                            row.get("H_KOR_CITY").asText() + " " + row.get("H_KOR_GU").asText()
                                                    + " " + row.get("H_KOR_DONG").asText()
                                    )
                            .build());
                }
            }

            return ResponseEntity.status(HttpStatus.OK).body(shopResponseDtoList);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }
    }
}
