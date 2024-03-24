package com.koreanguide.koreanguidebackend.domain.seoul.controller;

import com.koreanguide.koreanguidebackend.domain.auth.data.enums.SeoulCountry;
import com.koreanguide.koreanguidebackend.domain.seoul.service.SeoulService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/seoul")
@AllArgsConstructor
public class SeoulController {
    private final SeoulService seoulService;

    @GetMapping("/shop")
    public ResponseEntity<?> getSeoulShoppingList(@RequestParam SeoulCountry seoulCountry) {
        return seoulService.getSeoulShopList(seoulCountry);
    }
}
