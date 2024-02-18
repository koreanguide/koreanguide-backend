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
public class TrackUpdateRequestDto {
    private Long trackId;
    private String trackTitle;
    private String trackPreview;
    private String trackContent;
    private String primaryImageUrl;
    private boolean useAutoTranslate;
    private List<TrackImageApplyRequestDto> images;
    private List<TrackTagApplyRequestDto> tags;
}
