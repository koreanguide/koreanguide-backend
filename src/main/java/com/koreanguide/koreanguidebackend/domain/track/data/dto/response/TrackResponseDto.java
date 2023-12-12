package com.koreanguide.koreanguidebackend.domain.track.data.dto.response;

import com.koreanguide.koreanguidebackend.domain.auth.data.dto.response.BaseResponseDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.track.data.dto.request.TrackImageApplyRequestDto;
import com.koreanguide.koreanguidebackend.domain.track.data.dto.request.TrackTagApplyRequestDto;
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
    private boolean agreePublicTerms;
    private boolean agreeTerms;
    private boolean agreePrivacyPolicy;
    private String trackTitle;
    private String trackPreview;
    private String primaryImageUrl;
    private List<TrackImageApplyRequestDto> trackImageApplyRequestDtoList;
    private List<TrackTagApplyRequestDto> trackTagApplyRequestDtoList;
    private User user;
    private boolean visible;
}
