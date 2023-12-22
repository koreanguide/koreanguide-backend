package com.koreanguide.koreanguidebackend.domain.review.data.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewCommentRequestDto {
    private Long targetTrackId;
    private Long targetReviewId;
    private String content;
}
