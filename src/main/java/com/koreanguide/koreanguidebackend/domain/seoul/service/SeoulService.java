package com.koreanguide.koreanguidebackend.domain.seoul.service;

import com.koreanguide.koreanguidebackend.domain.auth.data.enums.SeoulCountry;
import org.springframework.http.ResponseEntity;

public interface SeoulService {
    ResponseEntity<?> getSeoulShopList(SeoulCountry seoulCountry);
}
