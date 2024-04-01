package com.koreanguide.koreanguidebackend.domain.saved.controller;

import com.koreanguide.koreanguidebackend.config.security.JwtTokenProvider;
import com.koreanguide.koreanguidebackend.domain.saved.data.dto.request.SavedRequestDto;
import com.koreanguide.koreanguidebackend.domain.saved.data.dto.response.SavedResponseDto;
import com.koreanguide.koreanguidebackend.domain.saved.service.SavedService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(tags = {"Saved API"})
@RestController
@RequestMapping("/api/v1/saved")
@RequiredArgsConstructor
public class SavedController {
    private final SavedService savedService;
    private final JwtTokenProvider jwtTokenProvider;

    public Long GET_USER_ID_BY_TOKEN(HttpServletRequest request) {
        return jwtTokenProvider.getUserIdByToken(request.getHeader("X-AUTH-TOKEN"));
    }

    @ApiOperation(value = "장바구니 아이템 추가")
    @PostMapping("/add")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> addItem(HttpServletRequest request,
                                     @RequestBody SavedRequestDto savedRequestDto) {
        return savedService.saveItem(GET_USER_ID_BY_TOKEN(request), savedRequestDto);
    }

    @ApiOperation(value = "장바구니 개수 불러오기")
    @GetMapping("/count")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public int getSavedCount(HttpServletRequest request) {
        return savedService.getSavedCount(GET_USER_ID_BY_TOKEN(request));
    }

    @ApiOperation(value = "장바구니 아이템 삭제")
    @DeleteMapping("/")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> removeSavedItem(HttpServletRequest request,
                                             @RequestParam Long itemId) {
        return savedService.removeSavedItem(GET_USER_ID_BY_TOKEN(request), itemId);
    }

    @ApiOperation(value = "장바구니 초기화")
    @DeleteMapping("/reset")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> resetSavedItem(HttpServletRequest request) {
        return savedService.resetSavedItem(GET_USER_ID_BY_TOKEN(request));
    }

    @ApiOperation(value = "장바구니 아이템 조회")
    @GetMapping("/")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public ResponseEntity<List<SavedResponseDto>> getSavedItem(HttpServletRequest request) {
        return savedService.getSavedItem(GET_USER_ID_BY_TOKEN(request));
    }
}