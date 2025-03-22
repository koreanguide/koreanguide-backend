package kr.yuns.seoul.data.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GeneratedTrackResponseDto {
    private String title;
    private String content;
    private String preview;
}