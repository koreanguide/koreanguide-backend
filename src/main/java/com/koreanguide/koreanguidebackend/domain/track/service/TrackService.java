package com.koreanguide.koreanguidebackend.domain.track.service;

import com.koreanguide.koreanguidebackend.domain.track.data.dto.request.TrackApplyRequestDto;
import com.koreanguide.koreanguidebackend.domain.track.data.dto.request.TrackRemoveRequestDto;
import com.koreanguide.koreanguidebackend.domain.track.data.dto.request.TrackUpdateRequestDto;
import org.springframework.http.ResponseEntity;

public interface TrackService {
    ResponseEntity<?> getAllTrack(Long userId);
    ResponseEntity<?> applyTrack(Long userId, TrackApplyRequestDto trackApplyRequestDto);

    ResponseEntity<?> updateTrack(Long userId, TrackUpdateRequestDto trackUpdateRequestDto);

    ResponseEntity<?> removeTrack(Long userId, TrackRemoveRequestDto trackRemoveRequestDto);

    ResponseEntity<?> setPrimaryTrack(Long userId, Long trackId);
}
