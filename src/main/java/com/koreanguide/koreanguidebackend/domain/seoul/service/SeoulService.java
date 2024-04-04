package com.koreanguide.koreanguidebackend.domain.seoul.service;

import com.koreanguide.koreanguidebackend.domain.auth.data.enums.SeoulCountry;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface SeoulService {
    ResponseEntity<?> getSeoulWeather(Long userId) throws IOException;

    ResponseEntity<?> getSeoulShopList(SeoulCountry seoulCountry);
}
