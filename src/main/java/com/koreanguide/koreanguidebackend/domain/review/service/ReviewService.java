package com.koreanguide.koreanguidebackend.domain.review.service;

import com.koreanguide.koreanguidebackend.domain.review.data.dto.request.ReviewCommentRequestDto;
import com.koreanguide.koreanguidebackend.domain.review.data.dto.request.ReviewRequestDto;
import org.springframework.http.ResponseEntity;

public interface ReviewService {
    ResponseEntity<?> getAllReview(Long userId);

    ResponseEntity<?> getReview(Long trackId);

    ResponseEntity<?> uploadReview(Long userId, ReviewRequestDto reviewRequestDto);

    ResponseEntity<?> uploadReviewComment(Long userId, ReviewCommentRequestDto reviewCommentRequestDto);

    ResponseEntity<?> deleteReview(Long userId, Long reviewId);
}
