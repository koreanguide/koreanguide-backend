package kr.yuns.track.service;

import org.springframework.http.ResponseEntity;

import kr.yuns.track.data.dto.request.TrackApplyRequestDto;
import kr.yuns.track.data.dto.request.TrackRemoveRequestDto;
import kr.yuns.track.data.dto.request.TrackUpdateRequestDto;

public interface TrackService {
    ResponseEntity<?> getAllTrack(Long userId);
    ResponseEntity<?> applyTrack(Long userId, TrackApplyRequestDto trackApplyRequestDto);
    ResponseEntity<?> updateTrack(Long userId, TrackUpdateRequestDto trackUpdateRequestDto);
    ResponseEntity<?> removeTrack(Long userId, TrackRemoveRequestDto trackRemoveRequestDto);
    ResponseEntity<?> setPrimaryTrack(Long userId, Long trackId);
    ResponseEntity<?> getTrackDeleteInfo(Long userId, Long trackId);
    ResponseEntity<?> getTrackEditInfo(Long userId, Long trackId);
    ResponseEntity<?> getTrackInfo(Long userId, Long trackId);
    ResponseEntity<?> getTopTrackUsedByMainPage();
}