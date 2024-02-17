package com.koreanguide.koreanguidebackend.domain.track.service;

import com.koreanguide.koreanguidebackend.domain.auth.data.dto.response.BaseResponseDto;
import com.koreanguide.koreanguidebackend.domain.track.data.dto.request.ChangeTrackTagRequestDto;
import com.koreanguide.koreanguidebackend.domain.track.data.dto.request.ChangeTrackValueRequestDto;
import com.koreanguide.koreanguidebackend.domain.track.data.dto.request.RemoveTrackRequestDto;
import com.koreanguide.koreanguidebackend.domain.track.data.dto.request.TrackApplyRequestDto;
import com.koreanguide.koreanguidebackend.domain.track.data.dto.response.TrackResponseDto;
import org.springframework.http.ResponseEntity;

public interface TrackService {
    ResponseEntity<?> getRandomTrack();

    ResponseEntity<?> getAllTrackInMainPages(Long userId);

    ResponseEntity<?> getAllTrackByUser(Long userId);
    ResponseEntity<TrackResponseDto> getTrackById(Long userId, Long trackId);
    ResponseEntity<BaseResponseDto> applyTrack(Long userId, TrackApplyRequestDto trackApplyRequestDto);
    ResponseEntity<?> changeTrackName(Long userId, ChangeTrackValueRequestDto changeTrackValueRequestDto);

    ResponseEntity<?> changeTrackPreview(Long userId, ChangeTrackValueRequestDto changeTrackValueRequestDto);

    ResponseEntity<?> changeTrackContent(Long userId, ChangeTrackValueRequestDto changeTrackValueRequestDto);

    ResponseEntity<?> changeTrackTag(Long userId, ChangeTrackTagRequestDto changeTrackTagRequestDto);

    ResponseEntity<?> removeTrack(Long userId, RemoveTrackRequestDto removeTrackRequestDto);

    ResponseEntity<BaseResponseDto> changeTracksVisible(Long userId, Long trackId);
    ResponseEntity<?> changeTrackStar(Long userId, Long trackId);
}
