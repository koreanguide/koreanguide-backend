package kr.yuns.seoul.service.Impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.yuns.auth.data.enums.SeoulCountry;
import kr.yuns.seoul.data.entity.DustData;
import kr.yuns.seoul.data.entity.SeoulCoordinate;
import kr.yuns.seoul.data.entity.WeatherData;
import kr.yuns.seoul.data.enums.DustInfo;
import kr.yuns.seoul.data.enums.SkyInfo;
import kr.yuns.seoul.service.CacheService;
import kr.yuns.seoul.service.SeoulFunctionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeoulFunctionServiceImpl implements SeoulFunctionService {
    private final CacheService cacheService;

    @Value("${seoul.api.key}")
    private String SEOUL_API_KEY;

    private String SEOUL_DUST_INFO_API = "http://openAPI.seoul.go.kr:8088/" + SEOUL_API_KEY + "/json/ListAirQualityByDistrictService/1/5/";
    private String SEOUL_KARAOKE_DONGDAEMUN_API = "http://openAPI.seoul.go.kr:8088/" + SEOUL_API_KEY + "/json/LOCALDATA_030901_DD/1/1000/";
    private String SEOUL_KARAOKE_SEOCHO_API = "http://openAPI.seoul.go.kr:8088/" + SEOUL_API_KEY + "/json/LOCALDATA_030901_SC/1/1000/";
    private String SEOUL_KARAOKE_GWANGJIN_API = "http://openAPI.seoul.go.kr:8088/" + SEOUL_API_KEY + "/json/LOCALDATA_030901_GJ/1/1000/";
    private String SEOUL_KARAOKE_SEONGDONG_API = "http://openAPI.seoul.go.kr:8088/" + SEOUL_API_KEY + "/json/LOCALDATA_030901_SD/1/1000/";
    private String SEOUL_KARAOKE_JUNGNANG_API = "http://openAPI.seoul.go.kr:8088/" + SEOUL_API_KEY + "/json/LOCALDATA_030901_JR/1/1000/";
    private String SEOUL_KARAOKE_MAPO_API = "http://openAPI.seoul.go.kr:8088/" + SEOUL_API_KEY + "/json/LOCALDATA_030901_MP/1/1000/";
    private String SEOUL_KARAOKE_GWANAK_API = "http://openAPI.seoul.go.kr:8088/" + SEOUL_API_KEY + "/json/LOCALDATA_030901_GA/1/1000/";
    private String SEOUL_KARAOKE_KANGSEO_API = "http://openAPI.seoul.go.kr:8088/" + SEOUL_API_KEY + "/json/LOCALDATA_030901_GS/1/1000/";
    private String SEOUL_KARAOKE_DOBONG_API = "http://openAPI.seoul.go.kr:8088/" + SEOUL_API_KEY + "/json/LOCALDATA_030901_DB/1/1000/";
    private String SEOUL_KARAOKE_EUNPYEONG_API = "http://openAPI.seoul.go.kr:8088/" + SEOUL_API_KEY + "/json/LOCALDATA_030901_EP/1/1000/";
    private String SEOUL_KARAOKE_GANGDONG_API = "http://openAPI.seoul.go.kr:8088/" + SEOUL_API_KEY + "/json/LOCALDATA_030901_GD/1/1000/";
    private String SEOUL_KARAOKE_JUNG_API = "http://openAPI.seoul.go.kr:8088/" + SEOUL_API_KEY + "/json/LOCALDATA_030901_JG/1/1000/";
    private String SEOUL_KARAOKE_YONGSAN_API = "http://openAPI.seoul.go.kr:8088/" + SEOUL_API_KEY + "/json/LOCALDATA_030901_YS/1/1000/";
    private String SEOUL_KARAOKE_JONGNO_API = "http://openAPI.seoul.go.kr:8088/" + SEOUL_API_KEY + "/json/LOCALDATA_030901_JN/1/1000/";
    private String SEOUL_KARAOKE_DONGJAK_API = "http://openAPI.seoul.go.kr:8088/" + SEOUL_API_KEY + "/json/LOCALDATA_030901_DJ/1/1000/";
    private String SEOUL_KARAOKE_YANGCHEON_API = "http://openAPI.seoul.go.kr:8088/" + SEOUL_API_KEY + "/json/LOCALDATA_030901_YC/1/1000/";
    private String SEOUL_KARAOKE_GANGBUK_API = "http://openAPI.seoul.go.kr:8088/" + SEOUL_API_KEY + "/json/LOCALDATA_030901_GB/1/1000/";
    private String SEOUL_KARAOKE_SEONGBUK_API = "http://openAPI.seoul.go.kr:8088/" + SEOUL_API_KEY + "/json/LOCALDATA_030901_SB/1/1000/";
    private String SEOUL_KARAOKE_GEUMCHEON_API = "http://openAPI.seoul.go.kr:8088/" + SEOUL_API_KEY + "/json/LOCALDATA_030901_GC/1/1000/";
    private String SEOUL_KARAOKE_SONGPA_API = "http://openAPI.seoul.go.kr:8088/" + SEOUL_API_KEY + "/json/LOCALDATA_030901_SP/1/1000/";
    private String SEOUL_KARAOKE_GURO_API = "http://openAPI.seoul.go.kr:8088/" + SEOUL_API_KEY + "/json/LOCALDATA_030901_GR/1/1000/";
    private String SEOUL_KARAOKE_GANGNAM_API = "http://openAPI.seoul.go.kr:8088/" + SEOUL_API_KEY + "/json/LOCALDATA_030901_GN/1/1000/";
    private String SEOUL_KARAOKE_NOWON_API = "http://openAPI.seoul.go.kr:8088/" + SEOUL_API_KEY + "/json/LOCALDATA_030901_NW/1/1000/";
    private String SEOUL_KARAOKE_YONGDENGPO_API = "http://openAPI.seoul.go.kr:8088/" + SEOUL_API_KEY + "/json/LOCALDATA_030901_YD/1/1000/";
    private String SEOUL_KARAOKE_SEODAEMUN_API = "http://openapi.seoul.go.kr:8088/" + SEOUL_API_KEY + "/json/LOCALDATA_030901_SM/1/1000/";

    @Override
    public String extractFromURL(String url) {
        Pattern pattern = Pattern.compile("LOCALDATA_030901_(\\w+)/");
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    @Override
    public String getKaraokeApiUrl(SeoulCountry country) {
        switch (country) {
            case DONGJAK:
                return SEOUL_KARAOKE_DONGJAK_API;
            case GURO:
                return SEOUL_KARAOKE_GURO_API;
            case JUNG:
                return SEOUL_KARAOKE_JUNG_API;
            case MAPO:
                return SEOUL_KARAOKE_MAPO_API;
            case NOWON:
                return SEOUL_KARAOKE_NOWON_API;
            case DOBONG:
                return SEOUL_KARAOKE_DOBONG_API;
            case GWANAK:
                return SEOUL_KARAOKE_GWANAK_API;
            case JONGNO:
                return SEOUL_KARAOKE_JONGNO_API;
            case SEOCHO:
                return SEOUL_KARAOKE_SEOCHO_API;
            case SONGPA:
                return SEOUL_KARAOKE_SONGPA_API;
            case GANGBUK:
                return SEOUL_KARAOKE_GANGBUK_API;
            case GANGSEO:
                return SEOUL_KARAOKE_KANGSEO_API;
            case YONGSAN:
                return SEOUL_KARAOKE_YONGSAN_API;
            case GANGDONG:
                return SEOUL_KARAOKE_GANGDONG_API;
            case GWANGJIN:
                return SEOUL_KARAOKE_GWANGJIN_API;
            case JUNGNANG:
                return SEOUL_KARAOKE_JUNGNANG_API;
            case SEONGBUK:
                return SEOUL_KARAOKE_SEONGBUK_API;
            case EUNPYEONG:
                return SEOUL_KARAOKE_EUNPYEONG_API;
            case GEUMCHEON:
                return SEOUL_KARAOKE_GEUMCHEON_API;
            case SEODAEMUN:
                return SEOUL_KARAOKE_SEODAEMUN_API;
            case SEONGDONG:
                return SEOUL_KARAOKE_SEONGDONG_API;
            case YANGCHEON:
                return SEOUL_KARAOKE_YANGCHEON_API;
            case DONGDAEMUN:
                return SEOUL_KARAOKE_DONGDAEMUN_API;
            case YONGDENGPO:
                return SEOUL_KARAOKE_YONGDENGPO_API;
            default:
                return SEOUL_KARAOKE_GANGNAM_API;
        }
    }

    @Override
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

    public String convertCountryToCode(SeoulCountry seoulCountry) {
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

    @Override
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

    @Override
    public DustInfo convertFineDustData(int data) {
        if(data >= 0 && data <= 30) {
            return DustInfo.GOOD;
        } else if (data > 30 && data <= 80) {
            return DustInfo.NORMAL;
        } else if (data > 80 && data <= 150) {
            return DustInfo.BAD;
        } else {
            return DustInfo.WORST;
        }
    }

    @Override
    public DustInfo convertUltraFineDustData(int data) {
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

    @Override
    public String convertKakaoMapUrl(String LAT, String LONG) {
        return "https://map.kakao.com/link/map/" + LAT + "," + LONG;
    }

    @Override
    public DustData callDustData(SeoulCountry seoulCountry) throws URISyntaxException {
        try {
            ObjectMapper mapper = new ObjectMapper();

            String COUNTRY_CODE = convertCountryToCode(seoulCountry);

            // 미세먼지 정보 캐시 저장
            String cacheKey = "DUST_DATA:" + COUNTRY_CODE;
            Optional<DustData> dustData = cacheService.getDustData(cacheKey);
            if (dustData.isPresent()) {
                return dustData.get();
            }

            URI uri = new URI(SEOUL_DUST_INFO_API + COUNTRY_CODE + "/");
            JsonNode root = mapper.readTree(uri.toURL());
            JsonNode rows = root.path("ListAirQualityByDistrictService").path("row");

            int PM_10_DATA = Integer.parseInt("0");
            int PM_25_DATA = Integer.parseInt("0");

            for (JsonNode row : rows) {
                PM_10_DATA = Integer.parseInt(row.get("PM10").asText());
                PM_25_DATA = Integer.parseInt(row.get("PM25").asText());
            }

            DustData newDustData = DustData.builder()
                        .fineDust(convertFineDustData(PM_10_DATA))
                        .ultraFineDust(convertUltraFineDustData(PM_25_DATA))
                    .build();

            log.info("fine dust: {}, ultra fine dust: {}", convertFineDustData(PM_10_DATA), convertUltraFineDustData(PM_25_DATA));

            cacheService.saveDustData(cacheKey, newDustData);

            return newDustData;
        } catch (IOException e) {
            return DustData.builder()
                        .fineDust(DustInfo.UNKNOWN)
                        .ultraFineDust(DustInfo.UNKNOWN)
                    .build();
        }
    }

    @Override
    public WeatherData callWeatherData(SeoulCountry seoulCountry) throws IOException {
        LocalDateTime CURRENT_TIME = LocalDateTime.now();
        LocalDateTime ADJUSTED_TIME = CURRENT_TIME.truncatedTo(ChronoUnit.HOURS);

        SeoulCoordinate seoulCoordinate = getSeoulCoordinate(seoulCountry);

        int hour = ADJUSTED_TIME.getHour();
        String base_date;
        String base_time;

        base_date = ADJUSTED_TIME.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        if (hour < 3) {
            ADJUSTED_TIME = ADJUSTED_TIME.minusDays(1);
            base_date = ADJUSTED_TIME.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            base_time = "2300";
        } else if (hour < 6) {
            base_time = "0200";
        } else if (hour < 9) {
            base_time = "0500";
        } else if (hour < 12) {
            base_time = "0800";
        } else if (hour < 15) {
            base_time = "1100";
        } else if (hour < 18) {
            base_time = "1400";
        } else if (hour < 21) {
            base_time = "1700";
        } else {
            base_time = "2000";
        }

        // 날씨 정보 캐시 저장
        String cacheKey = seoulCountry + ":" + base_date + base_time;
        Optional<WeatherData> cachedData = cacheService.getWeatherData(cacheKey);
        if (cachedData.isPresent()) {
            return cachedData.get();
        }

        String urlBuilder = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst" + "?" + URLEncoder.encode("serviceKey", StandardCharsets.UTF_8) + "=z2I9YCbpCq1a5T%2BxmhqssSL3zWq2IVBTYusxgVlwvOR3kwy9vgokbtJ8xRuArqGZ27DClJUkfIGdP9KGZvH%2FFw%3D%3D" +
                "&" + URLEncoder.encode("pageNo", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("1", StandardCharsets.UTF_8) +
                "&" + URLEncoder.encode("numOfRows", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("1000", StandardCharsets.UTF_8) +
                "&" + URLEncoder.encode("dataType", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("JSON", StandardCharsets.UTF_8) +
                "&" + URLEncoder.encode("base_date", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(base_date, StandardCharsets.UTF_8) +
                "&" + URLEncoder.encode("base_time", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(base_time, StandardCharsets.UTF_8) +
                "&" + URLEncoder.encode("nx", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(seoulCoordinate.getNx(), StandardCharsets.UTF_8) +
                "&" + URLEncoder.encode("ny", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(seoulCoordinate.getNy(), StandardCharsets.UTF_8);
        @SuppressWarnings("deprecation")
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

        cacheService.saveWeatherData(cacheKey, weatherData);

        return weatherData;
    }
}
