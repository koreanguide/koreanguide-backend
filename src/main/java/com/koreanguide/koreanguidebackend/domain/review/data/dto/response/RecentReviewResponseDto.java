package com.koreanguide.koreanguidebackend.domain.review.data.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecentReviewResponseDto {
    private String reviewUserName;
    private String reviewContent;
    private double star;
}
