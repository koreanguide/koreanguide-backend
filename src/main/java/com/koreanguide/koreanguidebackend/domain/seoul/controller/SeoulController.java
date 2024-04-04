package com.koreanguide.koreanguidebackend.domain.seoul.controller;

import com.koreanguide.koreanguidebackend.config.security.JwtTokenProvider;
import com.koreanguide.koreanguidebackend.domain.auth.data.enums.SeoulCountry;
import com.koreanguide.koreanguidebackend.domain.seoul.service.SeoulService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Api(tags = {"Seoul API"})
@RestController
@RequestMapping("/api/v1/seoul")
@AllArgsConstructor
public class SeoulController {
    private final SeoulService seoulService;
    private final JwtTokenProvider jwtTokenProvider;

    public Long GET_USER_ID_BY_TOKEN(HttpServletRequest request) {
        return jwtTokenProvider.getUserIdByToken(request.getHeader("X-AUTH-TOKEN"));
    }

    @GetMapping("/shop")
    @ApiOperation(value = "쇼핑몰 리스트 조회")
    public ResponseEntity<?> getSeoulShoppingList(@RequestParam SeoulCountry seoulCountry) {
        return seoulService.getSeoulShopList(seoulCountry);
    }

    @GetMapping("/weather")
    @ApiOperation(value = "날씨 정보 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> getSeoulWeather(HttpServletRequest request) throws IOException {
        return seoulService.getSeoulWeather(GET_USER_ID_BY_TOKEN(request));
    }
}
