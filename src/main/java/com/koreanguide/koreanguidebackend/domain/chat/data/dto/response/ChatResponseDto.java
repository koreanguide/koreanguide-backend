package com.koreanguide.koreanguidebackend.domain.chat.data.dto.response;

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
    private boolean useTrackFunction = false;
    private String trackPrimaryUrl = null;
    private String trackTitle = null;
    private String trackPreview = null;
    private Long trackId = null;
}
