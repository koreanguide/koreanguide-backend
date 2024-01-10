package com.koreanguide.koreanguidebackend.domain.track.data.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeTrackNameRequestDto {
    private Long trackId;
    private String newName;
}
