package kr.yuns.track.data.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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