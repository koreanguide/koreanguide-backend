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
public class TrackEditInfoResponseDto {
    private Long trackId;
    private String title;
    private String primaryImage;
    private List<String> additionalImage;
    private String preview;
    private List<String> tags;
    private String content;
    private boolean visible;
    private boolean useAutoTranslate;
}
