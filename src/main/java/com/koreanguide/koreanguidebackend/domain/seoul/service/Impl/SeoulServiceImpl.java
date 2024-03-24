package com.koreanguide.koreanguidebackend.domain.seoul.service.Impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.koreanguide.koreanguidebackend.domain.auth.data.enums.SeoulCountry;
import com.koreanguide.koreanguidebackend.domain.seoul.data.ShopResponseDto;
import com.koreanguide.koreanguidebackend.domain.seoul.service.SeoulService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeoulServiceImpl implements SeoulService {
    @Value("${seoul.api.key}")
    private String SEOUL_API_KEY;

    private final String SEOUL_SHOPPING_CENTER_LIST_API =
            "http://openapi.seoul.go.kr:8088/" + SEOUL_API_KEY + "/json/SebcShoppingCenterKor/1/1000/";

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
