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
public class TopTrackResponseDto {
    private Long trackId;
    private String title;
    private String preview;
    private String profileUrl;
    private String nickname;
    private Long view;
    private Long like;
    private List<String> tags;
}
