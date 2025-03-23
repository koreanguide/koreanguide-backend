package kr.yuns.chat.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatResponseDto {
//    프로필 URL
    private String profileUrl;
//    사용자 닉네임
    private String name;
//    메시지
    private String message;
//    전송 시간
    private LocalDateTime date;
    
    private Long senderId;

    @Builder.Default
    private boolean useTrackFunction = false;

    @Builder.Default
    private String trackPrimaryUrl = null;

    @Builder.Default
    private String trackTitle = null;

    @Builder.Default
    private String trackPreview = null;

    @Builder.Default
    private Long trackId = null;
}