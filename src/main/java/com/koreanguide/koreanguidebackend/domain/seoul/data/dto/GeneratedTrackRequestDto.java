package com.koreanguide.koreanguidebackend.domain.seoul.data.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GeneratedTrackRequestDto {
    private List<Long> savedId;
    private Long requiredSavedId;
    private boolean useHotelOptions;
    private boolean useChangeLocationOptions;
    private boolean useCanStartVisitorsLocationOptions;
}