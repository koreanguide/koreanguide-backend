package com.koreanguide.koreanguidebackend.domain.profile.data.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MainInfoResponseDto {
    private Long totalLiked;
    private boolean isIncreased;
    private Long increasedAmount;
    private Long totalView;
    private Long credit;
}
