package com.koreanguide.koreanguidebackend.domain.track.controller;

import com.koreanguide.koreanguidebackend.config.security.JwtTokenProvider;
import com.koreanguide.koreanguidebackend.domain.track.data.dto.request.TrackApplyRequestDto;
import com.koreanguide.koreanguidebackend.domain.track.data.dto.request.TrackRemoveRequestDto;
import com.koreanguide.koreanguidebackend.domain.track.data.dto.request.TrackUpdateRequestDto;
import com.koreanguide.koreanguidebackend.domain.track.service.TrackService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Api(tags = {"Track API"})
@RestController
@RequestMapping("/api/v1/track")
public class TrackController {
    private final TrackService trackService;
    private final JwtTokenProvider jwtTokenProvider;

    public Long GET_USER_ID_BY_TOKEN(HttpServletRequest request) {
        return jwtTokenProvider.getUserIdByToken(request.getHeader("X-AUTH-TOKEN"));
    }

    @Autowired
    public TrackController(TrackService trackService, JwtTokenProvider jwtTokenProvider) {
        this.trackService = trackService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @ApiOperation(value = "사용자 모든 트랙 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    @GetMapping("/")
    public ResponseEntity<?> getAllTrack(HttpServletRequest request) {
        return trackService.getAllTrack(GET_USER_ID_BY_TOKEN(request));
    }

    @ApiOperation(value = "트랙 등록")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    @PostMapping("/")
    public ResponseEntity<?> applyTrack(HttpServletRequest request,
                                                      @RequestBody TrackApplyRequestDto trackApplyRequestDto) {
        return trackService.applyTrack(GET_USER_ID_BY_TOKEN(request), trackApplyRequestDto);
    }

    @ApiOperation(value = "트랙 업데이트")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    @PutMapping("/")
    public ResponseEntity<?> updateTrack(HttpServletRequest request,
                                         @RequestBody TrackUpdateRequestDto trackUpdateRequestDto) {
        return trackService.updateTrack(GET_USER_ID_BY_TOKEN(request), trackUpdateRequestDto);
    }

    @ApiOperation(value = "트랙 수정 정보 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    @GetMapping("/edit")
    public ResponseEntity<?> getTrackEditInfo(HttpServletRequest request, @RequestParam Long trackId) {
        return trackService.getTrackEditInfo(GET_USER_ID_BY_TOKEN(request), trackId);
    }

    @ApiOperation(value = "트랙 정보 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    @GetMapping("/detail")
    public ResponseEntity<?> getTrackInfo(HttpServletRequest request, @RequestParam Long trackId) {
        return trackService.getTrackInfo(GET_USER_ID_BY_TOKEN(request), trackId);
    }

    @ApiOperation(value = "포털 메인페이지 TOP 3 트랙 조회")
    @GetMapping("/top")
    public ResponseEntity<?> getTopTrackUsedByMainPage() {
        return trackService.getTopTrackUsedByMainPage();
    }

    @ApiOperation(value = "트랙 삭제")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    @DeleteMapping("/")
    public ResponseEntity<?> removeTrack(HttpServletRequest request,
                                         @RequestBody TrackRemoveRequestDto trackRemoveRequestDto) {
        return trackService.removeTrack(GET_USER_ID_BY_TOKEN(request), trackRemoveRequestDto);
    }

    @ApiOperation(value = "대표 트랙 설정")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    @PostMapping("/star")
    public ResponseEntity<?> setPrimaryTrack(HttpServletRequest request, @RequestParam Long trackId) {
        return trackService.setPrimaryTrack(GET_USER_ID_BY_TOKEN(request), trackId);
    }

    @ApiOperation(value = "트랙 삭제 정보 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    @GetMapping("/deleteInfo")
    public ResponseEntity<?> getTrackDeleteInfo(HttpServletRequest request, @RequestParam Long trackId) {
        return trackService.getTrackDeleteInfo(GET_USER_ID_BY_TOKEN(request), trackId);
    }
}
