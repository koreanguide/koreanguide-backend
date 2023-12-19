package com.koreanguide.koreanguidebackend.domain.track.controller;

import com.koreanguide.koreanguidebackend.domain.auth.data.dto.response.BaseResponseDto;
import com.koreanguide.koreanguidebackend.domain.track.data.dto.request.TrackApplyRequestDto;
import com.koreanguide.koreanguidebackend.domain.track.data.dto.response.TrackResponseDto;
import com.koreanguide.koreanguidebackend.domain.track.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/track")
public class TrackController {
    private final TrackService trackService;

    @Autowired
    public TrackController(TrackService trackService) {
        this.trackService = trackService;
    }

    @PostMapping("/")
    public ResponseEntity<BaseResponseDto> applyTrack(@RequestParam Long userId,
                                                      @RequestBody TrackApplyRequestDto trackApplyRequestDto) {
        return trackService.applyTrack(userId, trackApplyRequestDto);
    }

    @GetMapping("/")
    public ResponseEntity<TrackResponseDto> getTrackById(@RequestParam Long userId, Long trackId) {
        return trackService.getTrackById(userId, trackId);
    }

    @PostMapping("/visible")
    public ResponseEntity<BaseResponseDto> changeTracksVisible(@RequestParam Long userId, Long trackId) {
        return trackService.changeTracksVisible(userId, trackId);
    }
}
