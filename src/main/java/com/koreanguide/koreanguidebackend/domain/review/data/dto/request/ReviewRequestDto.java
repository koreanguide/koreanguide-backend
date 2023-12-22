package com.koreanguide.koreanguidebackend.domain.review.data.dto.request;

import com.koreanguide.koreanguidebackend.domain.credit.data.enums.AccountProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewRequestDto {
    private Long targetTrackId;
    private String content;
}
