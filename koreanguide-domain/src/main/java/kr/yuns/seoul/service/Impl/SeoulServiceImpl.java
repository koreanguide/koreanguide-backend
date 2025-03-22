package kr.yuns.seoul.service.Impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.yuns.auth.data.dao.UserDao;
import kr.yuns.auth.data.entity.User;
import kr.yuns.auth.data.enums.SeoulCountry;
import kr.yuns.seoul.data.dto.response.AttractionsResponseDto;
import kr.yuns.seoul.data.dto.response.BicycleResponseDto;
import kr.yuns.seoul.data.dto.response.KaraokeResponseDto;
import kr.yuns.seoul.data.dto.response.ParkInfoResponseDto;
import kr.yuns.seoul.data.dto.response.ParkListResponseDto;
import kr.yuns.seoul.data.dto.response.ParkParkingInfoResponseDto;
import kr.yuns.seoul.data.dto.response.ParkResponseDto;
import kr.yuns.seoul.data.dto.response.ShopResponseDto;
import kr.yuns.seoul.data.dto.response.WeatherResponseDto;
import kr.yuns.seoul.data.entity.DustData;
import kr.yuns.seoul.data.entity.SeoulRiverPark;
import kr.yuns.seoul.data.entity.WeatherData;
import kr.yuns.seoul.data.enums.RiverPark;
import kr.yuns.seoul.data.repository.SeoulRiverParkRepository;
import kr.yuns.seoul.service.SeoulFunctionService;
import kr.yuns.seoul.service.SeoulService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeoulServiceImpl implements SeoulService {
    @Value("${seoul.api.key}")
    private String SEOUL_API_KEY;

    private final UserDao userDao;
    private final SeoulRiverParkRepository seoulRiverParkRepository;
    private final SeoulFunctionService seoulFunction;

    private String SEOUL_ATTRACTIONS_LIST_API = "http://openapi.seoul.go.kr:8088/" + SEOUL_API_KEY + "/json/SebcTourStreetKor/1/1000/";
    private String SEOUL_SHOPPING_CENTER_LIST_API = "http://openapi.seoul.go.kr:8088/" + SEOUL_API_KEY + "/json/SebcShoppingCenterKor/1/1000/";
    private String SEOUL_FOOD_LIST_API_1 = "http://openapi.seoul.go.kr:8088/" + SEOUL_API_KEY + "/json/SebcKoreanRestaurantsKor/1/1000/";
    private String SEOUL_FOOD_LIST_API_2 = "http://openapi.seoul.go.kr:8088/" + SEOUL_API_KEY + "/json/SebcKoreanRestaurantsKor/1001/1500/";
    private String SEOUL_BICYCLE_LIST_API_1 = "http://openapi.seoul.go.kr:8088/" + SEOUL_API_KEY + "/json/tbCycleStationInfo/1/1000/";
    private String SEOUL_BICYCLE_LIST_API_2 = "http://openapi.seoul.go.kr:8088/" + SEOUL_API_KEY + "/json/tbCycleStationInfo/1001/2000/";
    private String SEOUL_BICYCLE_LIST_API_3 = "http://openapi.seoul.go.kr:8088/" + SEOUL_API_KEY + "/json/tbCycleStationInfo/2001/3000/";
    private String SEOUL_BICYCLE_LIST_API_4 = "http://openapi.seoul.go.kr:8088/" + SEOUL_API_KEY + "/json/tbCycleStationInfo/3001/4000/";
    private String SEOUL_PARKING_API = "http://openAPI.seoul.go.kr:8088/" + SEOUL_API_KEY + "/json/TbParkingInfoView/1/30/";

    @Override
    public ResponseEntity<?> getSeoulWeather(Long userId) throws IOException, URISyntaxException {
        User user = userDao.getUserEntity(userId);
        WeatherData weatherData = seoulFunction.callWeatherData(user.getCountry());
        DustData dustData = seoulFunction.callDustData(user.getCountry());

        return ResponseEntity.status(HttpStatus.OK).body(WeatherResponseDto.builder()
                        .country(seoulFunction.getSeoulCountryName(user.getCountry()))
                        .minTemp(weatherData.getMinTemp())
                        .maxTemp(weatherData.getMaxTemp())
                        .nowTemp(weatherData.getNowTemp())
                        .sky(weatherData.getSky())
                        .findDust(dustData.getFineDust())
                        .ultrafineDust(dustData.getUltraFineDust())
                .build());
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

            @SuppressWarnings("deprecation")
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

            @SuppressWarnings("deprecation")
            JsonNode root = mapper.readTree(new URL(seoulFunction.getKaraokeApiUrl(seoulCountry)));
            JsonNode rows = root.path("LOCALDATA_030901_" + seoulFunction.extractFromURL(seoulFunction.getKaraokeApiUrl(seoulCountry))).path("row");

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

            @SuppressWarnings("deprecation")
            JsonNode root = mapper.readTree(new URL(SEOUL_SHOPPING_CENTER_LIST_API));
            JsonNode rows = root.path("SebcShoppingCenterKor").path("row");

            for (JsonNode row : rows) {
                String hKorGu = row.path("H_KOR_GU").asText();
                if (seoulFunction.getSeoulCountryName(seoulCountry).equals(hKorGu)) {
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

            @SuppressWarnings("deprecation")
            JsonNode JSON_NODE_FIRST_PAGE = mapper.readTree(new URL(SEOUL_FOOD_LIST_API_1));
            JsonNode JSON_NODE_FIRST_PAGE_ROWS = JSON_NODE_FIRST_PAGE.path("SebcKoreanRestaurantsKor").path("row");

            @SuppressWarnings("deprecation")
            JsonNode JSON_NODE_SECOND_PAGE = mapper.readTree(new URL(SEOUL_FOOD_LIST_API_2));
            JsonNode JSON_NODE_SECOND_PAGE_ROWS = JSON_NODE_SECOND_PAGE.path("SebcKoreanRestaurantsKor").path("row");

            for (JsonNode row : JSON_NODE_FIRST_PAGE_ROWS) {
                String hKorGu = row.path("H_KOR_GU").asText();
                if (seoulFunction.getSeoulCountryName(seoulCountry).equals(hKorGu)) {
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
                if (seoulFunction.getSeoulCountryName(seoulCountry).equals(hKorGu)) {
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

            @SuppressWarnings("deprecation")
            JsonNode JSON_NODE_FIRST_PAGE = mapper.readTree(new URL(SEOUL_BICYCLE_LIST_API_1));
            JsonNode JSON_NODE_FIRST_PAGE_ROWS = JSON_NODE_FIRST_PAGE.path("stationInfo").path("row");

            @SuppressWarnings("deprecation")
            JsonNode JSON_NODE_SECOND_PAGE = mapper.readTree(new URL(SEOUL_BICYCLE_LIST_API_2));
            JsonNode JSON_NODE_SECOND_PAGE_ROWS = JSON_NODE_SECOND_PAGE.path("stationInfo").path("row");

            @SuppressWarnings("deprecation")
            JsonNode JSON_NODE_THIRD_PAGE = mapper.readTree(new URL(SEOUL_BICYCLE_LIST_API_3));
            JsonNode JSON_NODE_THIRD_PAGE_ROWS = JSON_NODE_THIRD_PAGE.path("stationInfo").path("row");

            @SuppressWarnings("deprecation")
            JsonNode JSON_NODE_FOURTH_PAGE = mapper.readTree(new URL(SEOUL_BICYCLE_LIST_API_4));
            JsonNode JSON_NODE_FOURTH_PAGE_ROWS = JSON_NODE_FOURTH_PAGE.path("stationInfo").path("row");

            String COUNTRY_NAME = seoulFunction.getSeoulCountryName(seoulCountry);

            for (JsonNode row : JSON_NODE_FIRST_PAGE_ROWS) {
                String hKorGu = row.path("STA_LOC").asText();
                if (COUNTRY_NAME.equals(hKorGu)) {
                    bicycleResponseDtoList.add(BicycleResponseDto.builder()
                                .code(row.get("RENT_NO").asText())
                                .count(row.get("HOLD_NUM").asText())
                                .name(row.get("RENT_NM").asText())
                                .address(row.get("STA_ADD1").asText() + " " + row.get("STA_ADD2").asText())
                                .kakaoMapUrl(seoulFunction.convertKakaoMapUrl(
                                        row.get("STA_LAT").asText(), row.get("STA_LONG").asText())
                                )
                            .build());
                }
            }

            for (JsonNode row : JSON_NODE_SECOND_PAGE_ROWS) {
                String hKorGu = row.path("STA_LOC").asText();
                if (COUNTRY_NAME.equals(hKorGu)) {
                    bicycleResponseDtoList.add(BicycleResponseDto.builder()
                            .code(row.get("RENT_NO").asText())
                            .count(row.get("HOLD_NUM").asText())
                            .name(row.get("RENT_NM").asText())
                            .address(row.get("STA_ADD1").asText() + " " + row.get("STA_ADD2").asText())
                            .kakaoMapUrl(seoulFunction.convertKakaoMapUrl(
                                    row.get("STA_LAT").asText(), row.get("STA_LONG").asText())
                            )
                            .build());
                }
            }

            for (JsonNode row : JSON_NODE_THIRD_PAGE_ROWS) {
                String hKorGu = row.path("STA_LOC").asText();
                if (COUNTRY_NAME.equals(hKorGu)) {
                    bicycleResponseDtoList.add(BicycleResponseDto.builder()
                            .code(row.get("RENT_NO").asText())
                            .count(row.get("HOLD_NUM").asText())
                            .name(row.get("RENT_NM").asText())
                            .address(row.get("STA_ADD1").asText() + " " + row.get("STA_ADD2").asText())
                            .kakaoMapUrl(seoulFunction.convertKakaoMapUrl(
                                    row.get("STA_LAT").asText(), row.get("STA_LONG").asText())
                            )
                            .build());
                }
            }

            for (JsonNode row : JSON_NODE_FOURTH_PAGE_ROWS) {
                String hKorGu = row.path("STA_LOC").asText();
                if (COUNTRY_NAME.equals(hKorGu)) {
                    bicycleResponseDtoList.add(BicycleResponseDto.builder()
                            .code(row.get("RENT_NO").asText())
                            .count(row.get("HOLD_NUM").asText())
                            .name(row.get("RENT_NM").asText())
                            .address(row.get("STA_ADD1").asText() + " " + row.get("STA_ADD2").asText())
                            .kakaoMapUrl(seoulFunction.convertKakaoMapUrl(
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

            @SuppressWarnings("deprecation")
            JsonNode root = mapper.readTree(new URL(SEOUL_ATTRACTIONS_LIST_API));
            JsonNode rows = root.path("SebcTourStreetKor").path("row");

            for (JsonNode row : rows) {
                String hKorGu = row.path("H_KOR_GU").asText();
                if (seoulFunction.getSeoulCountryName(seoulCountry).equals(hKorGu)) {
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