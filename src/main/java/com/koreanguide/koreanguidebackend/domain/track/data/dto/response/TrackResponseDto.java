package com.koreanguide.koreanguidebackend.domain.track.data.dto.response;

import com.koreanguide.koreanguidebackend.domain.auth.data.dto.response.BaseResponseDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.track.data.dto.request.TrackImageApplyRequestDto;
import com.koreanguide.koreanguidebackend.domain.track.data.dto.request.TrackTagApplyRequestDto;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.TrackImage;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.TrackTag;
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
    private BaseResponseDto baseResponseDto;
    private String trackTitle;
    private String trackPreview;
    private String primaryImageUrl;
    private List<TrackImage> images;
    private List<TrackTag> tags;
    private String name;
    private String email;
    private boolean visible;
    private boolean blocked;
    private boolean star;
    private String blockedReason;
}
