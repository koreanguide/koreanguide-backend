package com.koreanguide.koreanguidebackend.domain.seoul.service;

import com.koreanguide.koreanguidebackend.domain.auth.data.enums.SeoulCountry;
import com.koreanguide.koreanguidebackend.domain.seoul.data.dto.AttractionsResponseDto;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

public interface SeoulService {
    ResponseEntity<?> getSeoulWeather(Long userId) throws IOException;

    ResponseEntity<?> getSeoulShopList(SeoulCountry seoulCountry);

    ResponseEntity<List<AttractionsResponseDto>> getAttractionsList(SeoulCountry seoulCountry);
}
