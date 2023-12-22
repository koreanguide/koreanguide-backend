package com.koreanguide.koreanguidebackend.domain.review.data.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewResponseDto {
    private String trackName;
    private String trackDescription;
    private String reviewUserProfileUrl;
    private String reviewUserName;
    private String reviewUserRegion;
    private String reviewContent;
    private boolean comment;
    private String reviewCommentContent;
}
