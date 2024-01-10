package com.koreanguide.koreanguidebackend.domain.track.service;

import com.koreanguide.koreanguidebackend.domain.auth.data.dto.response.BaseResponseDto;
import com.koreanguide.koreanguidebackend.domain.track.data.dto.request.ChangeTrackNameRequestDto;
import com.koreanguide.koreanguidebackend.domain.track.data.dto.request.TrackApplyRequestDto;
import com.koreanguide.koreanguidebackend.domain.track.data.dto.response.TrackResponseDto;
import org.springframework.http.ResponseEntity;

public interface TrackService {
    ResponseEntity<?> getRandomTrack();
    ResponseEntity<?> getAllTrackByUser(Long userId);
    ResponseEntity<TrackResponseDto> getTrackById(Long userId, Long trackId);
    ResponseEntity<BaseResponseDto> applyTrack(Long userId, TrackApplyRequestDto trackApplyRequestDto);
    ResponseEntity<?> changeTrackName(Long userId, ChangeTrackNameRequestDto changeTrackNameRequestDto);
    ResponseEntity<BaseResponseDto> changeTracksVisible(Long userId, Long trackId);
    ResponseEntity<?> changeTrackStar(Long userId, Long trackId);
}
