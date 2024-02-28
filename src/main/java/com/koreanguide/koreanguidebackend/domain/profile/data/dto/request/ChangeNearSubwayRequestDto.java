package com.koreanguide.koreanguidebackend.domain.profile.data.dto.request;

import com.koreanguide.koreanguidebackend.domain.profile.data.dto.enums.SubwayLine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeNearSubwayRequestDto {
    private SubwayLine subwayLine;
    private String station;
}
