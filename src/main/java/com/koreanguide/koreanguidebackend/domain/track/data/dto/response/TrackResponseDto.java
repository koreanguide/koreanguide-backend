package com.koreanguide.koreanguidebackend.domain.track.data.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrackResponseDto {
    private Long trackId;
    private String title;
    private String preview;
    private List<String> tags;
    private String primaryImage;
    private List<String> additionalImage;
    private String content;
    private Long like;
    private Long view;
}
