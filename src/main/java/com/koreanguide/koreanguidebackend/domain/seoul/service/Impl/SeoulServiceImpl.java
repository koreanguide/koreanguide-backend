package com.koreanguide.koreanguidebackend.domain.seoul.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.koreanguide.koreanguidebackend.domain.assistant.service.AssistantService;
import com.koreanguide.koreanguidebackend.domain.auth.data.dao.UserDao;
import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.auth.data.enums.SeoulCountry;
import com.koreanguide.koreanguidebackend.domain.cache.CacheService;
import com.koreanguide.koreanguidebackend.domain.saved.data.dao.SavedDao;
import com.koreanguide.koreanguidebackend.domain.saved.data.entity.Saved;
import com.koreanguide.koreanguidebackend.domain.saved.service.SavedService;
import com.koreanguide.koreanguidebackend.domain.seoul.data.dto.*;
import com.koreanguide.koreanguidebackend.domain.seoul.data.entity.SeoulRiverPark;
import com.koreanguide.koreanguidebackend.domain.seoul.data.enums.DustInfo;
import com.koreanguide.koreanguidebackend.domain.seoul.data.enums.RiverPark;
import com.koreanguide.koreanguidebackend.domain.seoul.data.enums.SkyInfo;
import com.koreanguide.koreanguidebackend.domain.seoul.data.entity.DustData;
import com.koreanguide.koreanguidebackend.domain.seoul.data.entity.SeoulCoordinate;
import com.koreanguide.koreanguidebackend.domain.seoul.data.entity.WeatherData;
import com.koreanguide.koreanguidebackend.domain.seoul.data.repository.SeoulRiverParkRepository;
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
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SeoulServiceImpl implements SeoulService {
    private final UserDao userDao;
    private final AssistantService assistantService;
    private final SavedDao savedDao;
    private final SavedService savedService;
    private final SeoulRiverParkRepository seoulRiverParkRepository;
    private final CacheService cacheService;
    private String SEOUL_API_KEY;
    private String SEOUL_SHOPPING_CENTER_LIST_API;
    private String SEOUL_ATTRACTIONS_LIST_API;
    private String SEOUL_FOOD_LIST_API_1;
    private String SEOUL_FOOD_LIST_API_2;
    private String SEOUL_BICYCLE_LIST_API_1;
    private String SEOUL_BICYCLE_LIST_API_2;
    private String SEOUL_BICYCLE_LIST_API_3;
    private String SEOUL_BICYCLE_LIST_API_4;
    private String SEOUL_DUST_INFO_API;
    private String SEOUL_KARAOKE_DONGDAEMUN_API;
    private String SEOUL_KARAOKE_SEOCHO_API;
    private String SEOUL_KARAOKE_GWANGJIN_API;
    private String SEOUL_KARAOKE_SEONGDONG_API;
    private String SEOUL_KARAOKE_NOWON_API;
    private String SEOUL_KARAOKE_JUNGNANG_API;
    private String SEOUL_KARAOKE_MAPO_API;
    private String SEOUL_KARAOKE_GWANAK_API;
    private String SEOUL_KARAOKE_KANGSEO_API;
    private String SEOUL_KARAOKE_DOBONG_API;
    private String SEOUL_KARAOKE_EUNPYEONG_API;
    private String SEOUL_KARAOKE_GANGDONG_API;
    private String SEOUL_KARAOKE_JUNG_API;
    private String SEOUL_KARAOKE_YONGSAN_API;
    private String SEOUL_KARAOKE_JONGNO_API;
    private String SEOUL_KARAOKE_DONGJAK_API;
    private String SEOUL_KARAOKE_YANGCHEON_API;
    private String SEOUL_KARAOKE_SEODAEMUN_API;
    private String SEOUL_KARAOKE_GANGBUK_API;
    private String SEOUL_KARAOKE_SEONGBUK_API;
    private String SEOUL_KARAOKE_GEUMCHEON_API;
    private String SEOUL_KARAOKE_SONGPA_API;
    private String SEOUL_KARAOKE_GURO_API;
    private String SEOUL_KARAOKE_GANGNAM_API;
    private String SEOUL_KARAOKE_YONGDENGPO_API;
    private String SEOUL_PARKING_API;

    public SeoulServiceImpl(UserDao userDao, AssistantService assistantService, SavedDao savedDao,
                            SeoulRiverParkRepository seoulRiverParkRepository,
                            SavedService savedService, CacheService cacheService, @Value("${seoul.api.key}") String SEOUL_API_KEY) {
        this.userDao = userDao;
        this.assistantService = assistantService;
        this.savedDao = savedDao;
        this.seoulRiverParkRepository = seoulRiverParkRepository;
        this.savedService = savedService;
        this.cacheService = cacheService;
        this.SEOUL_API_KEY = SEOUL_API_KEY;
        this.SEOUL_ATTRACTIONS_LIST_API = "http://openapi.seoul.go.kr:8088/" + this.SEOUL_API_KEY + "/json/SebcTourStreetKor/1/1000/";
        this.SEOUL_SHOPPING_CENTER_LIST_API = "http://openapi.seoul.go.kr:8088/" + this.SEOUL_API_KEY + "/json/SebcShoppingCenterKor/1/1000/";
        this.SEOUL_FOOD_LIST_API_1 = "http://openapi.seoul.go.kr:8088/" + this.SEOUL_API_KEY + "/json/SebcKoreanRestaurantsKor/1/1000/";
        this.SEOUL_FOOD_LIST_API_2 = "http://openapi.seoul.go.kr:8088/" + this.SEOUL_API_KEY + "/json/SebcKoreanRestaurantsKor/1001/1500/";
        this.SEOUL_BICYCLE_LIST_API_1 = "http://openapi.seoul.go.kr:8088/" + this.SEOUL_API_KEY + "/json/tbCycleStationInfo/1/1000/";
        this.SEOUL_BICYCLE_LIST_API_2 = "http://openapi.seoul.go.kr:8088/" + this.SEOUL_API_KEY + "/json/tbCycleStationInfo/1001/2000/";
        this.SEOUL_BICYCLE_LIST_API_3 = "http://openapi.seoul.go.kr:8088/" + this.SEOUL_API_KEY + "/json/tbCycleStationInfo/2001/3000/";
        this.SEOUL_BICYCLE_LIST_API_4 = "http://openapi.seoul.go.kr:8088/" + this.SEOUL_API_KEY + "/json/tbCycleStationInfo/3001/4000/";
        this.SEOUL_DUST_INFO_API = "http://openAPI.seoul.go.kr:8088/" + this.SEOUL_API_KEY + "/json/ListAirQualityByDistrictService/1/5/";
        this.SEOUL_KARAOKE_DONGDAEMUN_API = "http://openAPI.seoul.go.kr:8088/" + this.SEOUL_API_KEY + "/json/LOCALDATA_030901_DD/1/1000/";
        this.SEOUL_KARAOKE_SEOCHO_API = "http://openAPI.seoul.go.kr:8088/" + this.SEOUL_API_KEY + "/json/LOCALDATA_030901_SC/1/1000/";
        this.SEOUL_KARAOKE_GWANGJIN_API = "http://openAPI.seoul.go.kr:8088/" + this.SEOUL_API_KEY + "/json/LOCALDATA_030901_GJ/1/1000/";
        this.SEOUL_KARAOKE_SEONGDONG_API = "http://openAPI.seoul.go.kr:8088/" + this.SEOUL_API_KEY + "/json/LOCALDATA_030901_SD/1/1000/";
        this.SEOUL_KARAOKE_JUNGNANG_API = "http://openAPI.seoul.go.kr:8088/" + this.SEOUL_API_KEY + "/json/LOCALDATA_030901_JR/1/1000/";
        this.SEOUL_KARAOKE_MAPO_API = "http://openAPI.seoul.go.kr:8088/" + this.SEOUL_API_KEY + "/json/LOCALDATA_030901_MP/1/1000/";
        this.SEOUL_KARAOKE_GWANAK_API = "http://openAPI.seoul.go.kr:8088/" + this.SEOUL_API_KEY + "/json/LOCALDATA_030901_GA/1/1000/";
        this.SEOUL_KARAOKE_KANGSEO_API = "http://openAPI.seoul.go.kr:8088/" + this.SEOUL_API_KEY + "/json/LOCALDATA_030901_GS/1/1000/";
        this.SEOUL_KARAOKE_DOBONG_API = "http://openAPI.seoul.go.kr:8088/" + this.SEOUL_API_KEY + "/json/LOCALDATA_030901_DB/1/1000/";
        this.SEOUL_KARAOKE_EUNPYEONG_API = "http://openAPI.seoul.go.kr:8088/" + this.SEOUL_API_KEY + "/json/LOCALDATA_030901_EP/1/1000/";
        this.SEOUL_KARAOKE_GANGDONG_API = "http://openAPI.seoul.go.kr:8088/" + this.SEOUL_API_KEY + "/json/LOCALDATA_030901_GD/1/1000/";
        this.SEOUL_KARAOKE_JUNG_API = "http://openAPI.seoul.go.kr:8088/" + this.SEOUL_API_KEY + "/json/LOCALDATA_030901_JG/1/1000/";
        this.SEOUL_KARAOKE_YONGSAN_API = "http://openAPI.seoul.go.kr:8088/" + this.SEOUL_API_KEY + "/json/LOCALDATA_030901_YS/1/1000/";
        this.SEOUL_KARAOKE_JONGNO_API = "http://openAPI.seoul.go.kr:8088/" + this.SEOUL_API_KEY + "/json/LOCALDATA_030901_JN/1/1000/";
        this.SEOUL_KARAOKE_DONGJAK_API = "http://openAPI.seoul.go.kr:8088/" + this.SEOUL_API_KEY + "/json/LOCALDATA_030901_DJ/1/1000/";
        this.SEOUL_KARAOKE_YANGCHEON_API = "http://openAPI.seoul.go.kr:8088/" + this.SEOUL_API_KEY + "/json/LOCALDATA_030901_YC/1/1000/";
        this.SEOUL_KARAOKE_GANGBUK_API = "http://openAPI.seoul.go.kr:8088/" + this.SEOUL_API_KEY + "/json/LOCALDATA_030901_GB/1/1000/";
        this.SEOUL_KARAOKE_SEONGBUK_API = "http://openAPI.seoul.go.kr:8088/" + this.SEOUL_API_KEY + "/json/LOCALDATA_030901_SB/1/1000/";
        this.SEOUL_KARAOKE_GEUMCHEON_API = "http://openAPI.seoul.go.kr:8088/" + this.SEOUL_API_KEY + "/json/LOCALDATA_030901_GC/1/1000/";
        this.SEOUL_KARAOKE_SONGPA_API = "http://openAPI.seoul.go.kr:8088/" + this.SEOUL_API_KEY + "/json/LOCALDATA_030901_SP/1/1000/";
        this.SEOUL_KARAOKE_GURO_API = "http://openAPI.seoul.go.kr:8088/" + this.SEOUL_API_KEY + "/json/LOCALDATA_030901_GR/1/1000/";
        this.SEOUL_KARAOKE_GANGNAM_API = "http://openAPI.seoul.go.kr:8088/" + this.SEOUL_API_KEY + "/json/LOCALDATA_030901_GN/1/1000/";
        this.SEOUL_KARAOKE_NOWON_API = "http://openAPI.seoul.go.kr:8088/" + this.SEOUL_API_KEY + "/json/LOCALDATA_030901_NW/1/1000/";
        this.SEOUL_KARAOKE_YONGDENGPO_API = "http://openAPI.seoul.go.kr:8088/" + this.SEOUL_API_KEY + "/json/LOCALDATA_030901_YD/1/1000/";
        this.SEOUL_PARKING_API = "http://openAPI.seoul.go.kr:8088/" + this.SEOUL_API_KEY + "/json/TbParkingInfoView/1/30/";
    }

    private String TRACK_AUTO_OPTIONS_HOTEL = "소개글 마지막에, 이 내용을 추가할거야. 강남구에 방문하시는 분들을 위해 호" +
            "텔을 추천해 드릴게요. AA호텔, BB호텔, CC호텔의 호텔이 있으니 관광에 참고하시면 좋을 것 같아요!";

    private String TRACK_AUTO_OPTIONS_LOCATION = "소개글 마지막에, 이 내용을 추가할거야. 혹시 관광 다른 관광 장" +
            "소를 원한다면 채팅을 통해 함께 협의 후 장소를 변경하거나 추가할 수 있으니 채팅을 부탁드려요!";

    private String TRACK_AUTO_OPTIONS_START_LOCATION = "소개글 마지막에, 이 내용을 추가할거야. 혹시 관광을 시작할 때 만남을 원하" +
            "시는 장소가 있으면 그 곳으로 이동해 드릴 수 있어요! 채팅으로 말씀해 주세요!";

    private String TRACK_AUTO_SYSTEM_CONTENTS = "이 서비스는 한국에 방문하는 외국인과 실제 한국인이 만나서 한국에 대해 깊이 소개해" +
            "줄 수 있는 매칭 서비스야. 너는 사용자가 요청한 3가지에 대해 함께 할 여정에 대한 본문을 작성할거야." +
            "타이틀을 달 필요도 없고 그냥 본문만 작성해주면 돼." +
            "그리고 굳이 영어로 작성해 주지 않아도 돼. 반말로 작성하지마. 최대한 글을 길게 써줘. 인사말부터 본문, 마무리 인사까지. 최소 2,000자" +
            "이상으로 작성되어야 해.";

    private String TRACK_AUTO_SYSTEM_TITLE = "이 서비스는 한국에 방문하는 외국인과 실제 한국인이 만나서 한국에 대해 깊이 " +
            "소개해 줄 수 있는 매칭 서비스야. 이 글에 대해서 짧고 굵은 제목을 작성해줄래? 제목은 30자 이내로 작성해줘. " +
            "타이틀을 달 필요도 없고 그냥 본문만 작성해주면 돼.";

    private String TRACK_AUTO_SYSTEM_PREVIEW = "이 서비스는 한국에 방문하는 외국인과 실제 한국인이 만나서 한국에 대해 깊이 " +
            "소개해 줄 수 있는 매칭 서비스야. 이 글에 대해서 간단한 소개글을 작성해 줄래? 소개글은 50자 이내로 작성해줘. " +
            "타이틀을 달 필요도 없고 그냥 본문만 작성해주면 돼.";

    public String extractFromURL(String url) {
        Pattern pattern = Pattern.compile("LOCALDATA_030901_(\\w+)/");
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    public String GET_TRACK_AUTO_CONTENT(String category, String value, String address, boolean required) {
        String GENERATED_CONTENTS = "이 카테고리의 분류는 " + category + "이야." + "여기서 " + value + "라는 장소에 방문할거야." + "주소는 " + address + "이야.";
        if(!required) {
            return GENERATED_CONTENTS;
        } else {
            return GENERATED_CONTENTS + "이 곳은 반드시 관광객과 방문할거야.";
        }
    }

    public String getKaraokeApiUrl(SeoulCountry seoulCountry) {
        switch (seoulCountry) {
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

    public DustInfo CONVERT_FINE_DUST_DATA(int data) {
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

    public DustInfo CONVERT_ULTRA_FINE_DUST_DATA(int data) {
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

    public String CONVERT_COORDINATE_TO_KAKAO_MAP_URL(String LAT, String LONG) {
        return "https://map.kakao.com/link/map/" + LAT + "," + LONG;
    }

    public DustData CALL_DUST_DATA(SeoulCountry seoulCountry) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            String COUNTRY_CODE = CONVERT_COUNTRY_TO_CODE(seoulCountry);

            // 미세먼지 정보 캐시 저장
            String cacheKey = "DUST_DATA:" + COUNTRY_CODE;
            Optional<DustData> dustData = cacheService.getDustData(cacheKey);
            if (dustData.isPresent()) {
                return dustData.get();
            }

            JsonNode root = mapper.readTree(new URL(SEOUL_DUST_INFO_API + COUNTRY_CODE + "/"));
            JsonNode rows = root.path("ListAirQualityByDistrictService").path("row");

            int PM_10_DATA = Integer.parseInt("0");
            int PM_25_DATA = Integer.parseInt("0");

            for (JsonNode row : rows) {
                PM_10_DATA = Integer.parseInt(row.get("PM10").asText());
                PM_25_DATA = Integer.parseInt(row.get("PM25").asText());
            }

            DustData newDustData = DustData.builder()
                        .fineDust(CONVERT_FINE_DUST_DATA(PM_10_DATA))
                        .ultraFineDust(CONVERT_ULTRA_FINE_DUST_DATA(PM_25_DATA))
                    .build();

            cacheService.saveDustData(cacheKey, newDustData);

            return newDustData;
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
    public ResponseEntity<GeneratedTrackResponseDto> getAutoGeneratedTrack(
            Long userId, GeneratedTrackRequestDto generatedTrackRequestDto) throws JsonProcessingException {
        try {
            StringBuilder USER_CONTENTS = new StringBuilder();
            for (int i = 0; i < generatedTrackRequestDto.getSavedId().size(); i++) {
                Saved saved = savedDao.getSavedEntity(generatedTrackRequestDto.getSavedId().get(i));
                USER_CONTENTS.append(GET_TRACK_AUTO_CONTENT(saved.getCategory(), saved.getValue(), saved.getAddress(),
                        saved.getId().equals(generatedTrackRequestDto.getRequiredSavedId())));
            }

            if(generatedTrackRequestDto.isUseChangeLocationOptions()) {
                USER_CONTENTS.append(TRACK_AUTO_OPTIONS_HOTEL);
            }

            if(generatedTrackRequestDto.isUseCanStartVisitorsLocationOptions()) {
                USER_CONTENTS.append(TRACK_AUTO_OPTIONS_START_LOCATION);
            }

            if(generatedTrackRequestDto.isUseHotelOptions()) {
                USER_CONTENTS.append(TRACK_AUTO_OPTIONS_LOCATION);
            }

            JsonNode GPT_CONTENTS_JSON_NODE = assistantService.callChatGpt(TRACK_AUTO_SYSTEM_CONTENTS, USER_CONTENTS.toString());
            String GENERATED_TRACK_CONTENTS = GPT_CONTENTS_JSON_NODE.path("choices").get(0).path("message").path("content").asText();

            JsonNode GPT_TITLE_JSON_NODE = assistantService.callChatGpt(
                    TRACK_AUTO_SYSTEM_TITLE, GENERATED_TRACK_CONTENTS);

            JsonNode GPT_PREVIEW_JSON_NODE = assistantService.callChatGpt(
                    TRACK_AUTO_SYSTEM_PREVIEW, GENERATED_TRACK_CONTENTS);

            String GENERATED_TRACK_TITLE = GPT_TITLE_JSON_NODE.path("choices").get(0).path("message").path("content").asText();
            String GENERATED_TRACK_PREVIEW = GPT_PREVIEW_JSON_NODE.path("choices").get(0).path("message").path("content").asText();

            // GPT 호출 로그 저장
            assistantService.saveAssistantLog(USER_CONTENTS.toString(), GPT_CONTENTS_JSON_NODE, userId);
            assistantService.saveAssistantLog(GENERATED_TRACK_CONTENTS, GPT_TITLE_JSON_NODE, userId);
            assistantService.saveAssistantLog(GENERATED_TRACK_CONTENTS, GPT_PREVIEW_JSON_NODE, userId);

            // 사용자 장바구니 초기화
            savedService.resetSavedItem(userId);

            return ResponseEntity.status(HttpStatus.OK).body(GeneratedTrackResponseDto.builder()
                    .title(GENERATED_TRACK_TITLE)
                    .preview(GENERATED_TRACK_PREVIEW)
                    .content(GENERATED_TRACK_CONTENTS)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Override
    public ResponseEntity<?> getRiverInfo(RiverPark riverPark) {
        try {
            SeoulRiverPark seoulRiverPark = seoulRiverParkRepository.getByParkEn(riverPark);
            ParkInfoResponseDto parkInfoResponseDto = new ParkInfoResponseDto();
            parkInfoResponseDto.setPhoneNum(seoulRiverPark.getParkService());
            parkInfoResponseDto.setLength(seoulRiverPark.getParklength());
            parkInfoResponseDto.setAddress(seoulRiverPark.getParkAddress());
            parkInfoResponseDto.setArea(seoulRiverPark.getParkArea());
            parkInfoResponseDto.setName(seoulRiverPark.getParkKo());
            parkInfoResponseDto.setParkX(seoulRiverPark.getParkX());
            parkInfoResponseDto.setParkY(seoulRiverPark.getParkY());

            List<ParkParkingInfoResponseDto> parkParkingInfoResponseDtoList = new ArrayList<>();

            ObjectMapper mapper = new ObjectMapper();

            JsonNode root = mapper.readTree(new URL(SEOUL_PARKING_API));
            JsonNode rows = root.path("TbParkingInfoView").path("row");

            for (JsonNode row : rows) {
                String hKorGu = row.path("PARKING_DIV_CD").asText();
                if (seoulRiverPark.getParkKo().equals(hKorGu)) {
                    parkParkingInfoResponseDtoList.add(ParkParkingInfoResponseDto.builder()
                                .name(row.get("PARKING_NM").asText())
                                .address(row.get("ADDRESS").asText())
                                .available(row.get("CELL_CNT").asText())
                                .week(row.get("WDAYS_START_TM").asText() + "~" + row.get("WDAYS_END_TM").asText())
                                .weekend(row.get("WEND_START_TM").asText() + "~" + row.get("WEND_END_TM").asText())
                                .startFee(row.get("DFLT_AMT").asText())
                                .additionalFee(row.get("INTVL_AMT").asText())
                                .phoneNum(row.get("TEL_NO").asText())
                            .build());
                }
            }

            parkInfoResponseDto.setParkingData(parkParkingInfoResponseDtoList);

            return ResponseEntity.status(HttpStatus.OK).body(parkInfoResponseDto);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }
    }

    @Override
    public ResponseEntity<?> getSeoulKaraokeList(SeoulCountry seoulCountry) {
        try {
            List<KaraokeResponseDto> karaokeResponseDtoList = new ArrayList<>();
            ObjectMapper mapper = new ObjectMapper();

            JsonNode root = mapper.readTree(new URL(getKaraokeApiUrl(seoulCountry)));
            JsonNode rows = root.path("LOCALDATA_030901_" + extractFromURL(getKaraokeApiUrl(seoulCountry))).path("row");

            for (JsonNode row : rows) {
                String hKorGu = row.path("TRDSTATENM").asText();
                if (hKorGu.equals("영업/정상")) {
                    karaokeResponseDtoList.add(KaraokeResponseDto.builder()
                            .address(row.get("SITEWHLADDR").asText())
                            .name(row.get("BPLCNM").asText())
                            .phoneNum(
                                    row.get("SITETEL").asText().isEmpty() ? "정보없음" :
                                            row.get("SITETEL").asText()
                            )
                            .build());
                }
            }

            return ResponseEntity.status(HttpStatus.OK).body(karaokeResponseDtoList);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }
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

    @Override
    public ResponseEntity<?> getSeoulFoodList(SeoulCountry seoulCountry) {
        try {
            List<ShopResponseDto> shopResponseDtoList = new ArrayList<>();
            ObjectMapper mapper = new ObjectMapper();

            JsonNode JSON_NODE_FIRST_PAGE = mapper.readTree(new URL(SEOUL_FOOD_LIST_API_1));
            JsonNode JSON_NODE_FIRST_PAGE_ROWS = JSON_NODE_FIRST_PAGE.path("SebcKoreanRestaurantsKor").path("row");

            JsonNode JSON_NODE_SECOND_PAGE = mapper.readTree(new URL(SEOUL_FOOD_LIST_API_2));
            JsonNode JSON_NODE_SECOND_PAGE_ROWS = JSON_NODE_SECOND_PAGE.path("SebcKoreanRestaurantsKor").path("row");

            for (JsonNode row : JSON_NODE_FIRST_PAGE_ROWS) {
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

            for (JsonNode row : JSON_NODE_SECOND_PAGE_ROWS) {
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

    @Override
    public ResponseEntity<List<BicycleResponseDto>> getSeoulBicycleList(SeoulCountry seoulCountry) {
        try {
            List<BicycleResponseDto> bicycleResponseDtoList = new ArrayList<>();
            ObjectMapper mapper = new ObjectMapper();

            JsonNode JSON_NODE_FIRST_PAGE = mapper.readTree(new URL(SEOUL_BICYCLE_LIST_API_1));
            JsonNode JSON_NODE_FIRST_PAGE_ROWS = JSON_NODE_FIRST_PAGE.path("stationInfo").path("row");

            JsonNode JSON_NODE_SECOND_PAGE = mapper.readTree(new URL(SEOUL_BICYCLE_LIST_API_2));
            JsonNode JSON_NODE_SECOND_PAGE_ROWS = JSON_NODE_SECOND_PAGE.path("stationInfo").path("row");

            JsonNode JSON_NODE_THIRD_PAGE = mapper.readTree(new URL(SEOUL_BICYCLE_LIST_API_3));
            JsonNode JSON_NODE_THIRD_PAGE_ROWS = JSON_NODE_THIRD_PAGE.path("stationInfo").path("row");

            JsonNode JSON_NODE_FOURTH_PAGE = mapper.readTree(new URL(SEOUL_BICYCLE_LIST_API_4));
            JsonNode JSON_NODE_FOURTH_PAGE_ROWS = JSON_NODE_FOURTH_PAGE.path("stationInfo").path("row");

            for (JsonNode row : JSON_NODE_FIRST_PAGE_ROWS) {
                String hKorGu = row.path("STA_LOC").asText();
                if (getSeoulCountryName(seoulCountry).equals(hKorGu)) {
                    bicycleResponseDtoList.add(BicycleResponseDto.builder()
                                .code(row.get("RENT_NO").asText())
                                .count(row.get("HOLD_NUM").asText())
                                .name(row.get("RENT_NM").asText())
                                .address(row.get("STA_ADD1").asText() + " " + row.get("STA_ADD2").asText())
                                .kakaoMapUrl(CONVERT_COORDINATE_TO_KAKAO_MAP_URL(
                                        row.get("STA_LAT").asText(), row.get("STA_LONG").asText())
                                )
                            .build());
                }
            }

            for (JsonNode row : JSON_NODE_SECOND_PAGE_ROWS) {
                String hKorGu = row.path("STA_LOC").asText();
                if (getSeoulCountryName(seoulCountry).equals(hKorGu)) {
                    bicycleResponseDtoList.add(BicycleResponseDto.builder()
                            .code(row.get("RENT_NO").asText())
                            .count(row.get("HOLD_NUM").asText())
                            .name(row.get("RENT_NM").asText())
                            .address(row.get("STA_ADD1").asText() + " " + row.get("STA_ADD2").asText())
                            .kakaoMapUrl(CONVERT_COORDINATE_TO_KAKAO_MAP_URL(
                                    row.get("STA_LAT").asText(), row.get("STA_LONG").asText())
                            )
                            .build());
                }
            }

            for (JsonNode row : JSON_NODE_THIRD_PAGE_ROWS) {
                String hKorGu = row.path("STA_LOC").asText();
                if (getSeoulCountryName(seoulCountry).equals(hKorGu)) {
                    bicycleResponseDtoList.add(BicycleResponseDto.builder()
                            .code(row.get("RENT_NO").asText())
                            .count(row.get("HOLD_NUM").asText())
                            .name(row.get("RENT_NM").asText())
                            .address(row.get("STA_ADD1").asText() + " " + row.get("STA_ADD2").asText())
                            .kakaoMapUrl(CONVERT_COORDINATE_TO_KAKAO_MAP_URL(
                                    row.get("STA_LAT").asText(), row.get("STA_LONG").asText())
                            )
                            .build());
                }
            }

            for (JsonNode row : JSON_NODE_FOURTH_PAGE_ROWS) {
                String hKorGu = row.path("STA_LOC").asText();
                if (getSeoulCountryName(seoulCountry).equals(hKorGu)) {
                    bicycleResponseDtoList.add(BicycleResponseDto.builder()
                            .code(row.get("RENT_NO").asText())
                            .count(row.get("HOLD_NUM").asText())
                            .name(row.get("RENT_NM").asText())
                            .address(row.get("STA_ADD1").asText() + " " + row.get("STA_ADD2").asText())
                            .kakaoMapUrl(CONVERT_COORDINATE_TO_KAKAO_MAP_URL(
                                    row.get("STA_LAT").asText(), row.get("STA_LONG").asText())
                            )
                            .build());
                }
            }

            return ResponseEntity.status(HttpStatus.OK).body(bicycleResponseDtoList);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }
    }

    @Override
    public ResponseEntity<List<AttractionsResponseDto>> getAttractionsList(SeoulCountry seoulCountry) {
        try {
            List<AttractionsResponseDto> attractionsResponseDtoList = new ArrayList<>();
            ObjectMapper mapper = new ObjectMapper();

            JsonNode root = mapper.readTree(new URL(SEOUL_ATTRACTIONS_LIST_API));
            JsonNode rows = root.path("SebcTourStreetKor").path("row");

            for (JsonNode row : rows) {
                String hKorGu = row.path("H_KOR_GU").asText();
                if (getSeoulCountryName(seoulCountry).equals(hKorGu)) {
                    attractionsResponseDtoList.add(AttractionsResponseDto.builder()
                            .latitude(row.get("WGS84_X").asDouble())
                            .longitude(row.get("WGS84_Y").asDouble())
                            .address(row.get("ADD_KOR").asText())
                            .tag(row.get("LAW_HEMD").asText())
                            .title(row.get("NAME_KOR").asText())
                    .build());
                }
            }

            return ResponseEntity.status(HttpStatus.OK).body(attractionsResponseDtoList);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }
    }

    @Override
    public ResponseEntity<ParkResponseDto> getSeoulRiverParkList(SeoulCountry seoulCountry) {
        List<ParkListResponseDto> dataList = new ArrayList<>();
        List<ParkListResponseDto> recomendList = new ArrayList<>();

        List<SeoulRiverPark> seoulRiverParkList = seoulRiverParkRepository.findAll();

        for(SeoulRiverPark seoulRiverPark : seoulRiverParkList) {
            if(seoulRiverPark.getParkCountry().equals(seoulCountry)) {
                dataList.add(ParkListResponseDto.builder()
                                .name(seoulRiverPark.getParkKo())
                                .address("서울특별시 " + seoulRiverPark.getParkAddress())
                                .riverPark(seoulRiverPark.getParkEn())
                        .build());
            } else {
                recomendList.add(ParkListResponseDto.builder()
                        .name(seoulRiverPark.getParkKo())
                        .address("서울특별시 " + seoulRiverPark.getParkAddress())
                        .riverPark(seoulRiverPark.getParkEn())
                        .build());
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(ParkResponseDto.builder()
                        .data(dataList)
                        .recommend(recomendList)
                .build());
    }
}
