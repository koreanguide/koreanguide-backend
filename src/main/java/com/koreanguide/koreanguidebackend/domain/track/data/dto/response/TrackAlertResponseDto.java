package com.koreanguide.koreanguidebackend.domain.track.data.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Builder
public class TrackAlertResponseDto {
    private String en;
    private String ko;
}