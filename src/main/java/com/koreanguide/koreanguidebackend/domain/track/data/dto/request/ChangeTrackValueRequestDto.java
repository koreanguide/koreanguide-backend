package com.koreanguide.koreanguidebackend.domain.track.data.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeTrackValueRequestDto {
    private Long trackId;
    private String changeValue;
}
