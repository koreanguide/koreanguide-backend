package com.koreanguide.koreanguidebackend.domain.saved.service;

import com.koreanguide.koreanguidebackend.domain.saved.data.dto.request.SavedRequestDto;
import com.koreanguide.koreanguidebackend.domain.saved.data.dto.response.SavedResponseDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SavedService {
    ResponseEntity<?> saveItem(Long userId, SavedRequestDto savedRequestDto);

    int getSavedCount(Long userId);

    ResponseEntity<?> removeSavedItem(Long userId, Long itemId);

    ResponseEntity<?> resetSavedItem(Long userId);

    ResponseEntity<List<SavedResponseDto>> getSavedItem(Long userId);
}
