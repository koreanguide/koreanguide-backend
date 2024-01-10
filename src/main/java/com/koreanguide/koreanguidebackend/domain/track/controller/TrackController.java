package com.koreanguide.koreanguidebackend.domain.track.controller;

import com.koreanguide.koreanguidebackend.config.security.JwtTokenProvider;
import com.koreanguide.koreanguidebackend.domain.auth.data.dto.response.BaseResponseDto;
import com.koreanguide.koreanguidebackend.domain.track.data.dto.request.ChangeTrackNameRequestDto;
import com.koreanguide.koreanguidebackend.domain.track.data.dto.request.TrackApplyRequestDto;
import com.koreanguide.koreanguidebackend.domain.track.data.dto.response.TrackResponseDto;
import com.koreanguide.koreanguidebackend.domain.track.service.TrackService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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

    @GetMapping("/random")
    public ResponseEntity<?> getRandomTrack() {
        return trackService.getRandomTrack();
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    @GetMapping("/all")
    public ResponseEntity<?> getAllTrackByUser(HttpServletRequest request) {
        return trackService.getAllTrackByUser(GET_USER_ID_BY_TOKEN(request));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    @PostMapping("/")
    public ResponseEntity<BaseResponseDto> applyTrack(HttpServletRequest request,
                                                      @RequestBody TrackApplyRequestDto trackApplyRequestDto) {
        return trackService.applyTrack(GET_USER_ID_BY_TOKEN(request), trackApplyRequestDto);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    @GetMapping("/")
    public ResponseEntity<TrackResponseDto> getTrackById(HttpServletRequest request, @RequestParam Long trackId) {
        return trackService.getTrackById(GET_USER_ID_BY_TOKEN(request), trackId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    @PutMapping("/name")
    public ResponseEntity<?> changeTrackName(HttpServletRequest request,
                                             @RequestBody ChangeTrackNameRequestDto changeTrackNameRequestDto) {
        return trackService.changeTrackName(GET_USER_ID_BY_TOKEN(request), changeTrackNameRequestDto);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    @PostMapping("/visible")
    public ResponseEntity<BaseResponseDto> changeTracksVisible(HttpServletRequest request, @RequestParam Long trackId) {
        return trackService.changeTracksVisible(GET_USER_ID_BY_TOKEN(request), trackId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    @PostMapping("/star")
    public ResponseEntity<?> changeTrackStar(HttpServletRequest request, @RequestParam Long trackId) {
        return trackService.changeTrackStar(GET_USER_ID_BY_TOKEN(request), trackId
        );
    }
}
