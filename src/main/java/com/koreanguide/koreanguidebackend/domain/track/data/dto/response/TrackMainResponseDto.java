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
public class TrackMainResponseDto {
    private String trackTitle;
    private String trackPreview;
    private String primaryImageUrl;
    private List<String> tags;
    private boolean star;
}
