package com.koreanguide.koreanguidebackend.domain.track.data.dto.request;

import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.credit.data.enums.AccountProvider;
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
public class TrackApplyRequestDto {
    private boolean agreePublicTerms;
    private boolean agreeTerms;
    private boolean agreePrivacyPolicy;
    private String trackTitle;
    private String trackPreview;
    private String trackContent;
    private String primaryImageUrl;
    private boolean useAutoTranslate;
    private List<TrackImageApplyRequestDto> images;
    private List<TrackTagApplyRequestDto> tags;
}
