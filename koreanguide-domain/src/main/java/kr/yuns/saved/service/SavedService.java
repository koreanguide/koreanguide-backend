package kr.yuns.saved.service;

import org.springframework.http.ResponseEntity;

import kr.yuns.saved.data.dto.request.SavedRequestDto;
import kr.yuns.saved.data.dto.response.SavedResponseDto;

import java.util.List;

public interface SavedService {
    ResponseEntity<?> saveItem(Long userId, SavedRequestDto savedRequestDto);
    int getSavedCount(Long userId);
    ResponseEntity<?> removeSavedItem(Long userId, Long itemId);
    ResponseEntity<?> resetSavedItem(Long userId);
    ResponseEntity<List<SavedResponseDto>> getSavedItem(Long userId);
}