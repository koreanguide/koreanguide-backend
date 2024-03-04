package com.koreanguide.koreanguidebackend.domain.chat.data.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatListResponseDto {
    private String profileUrl;
    private String name;
    private String lastTalkedAt;
    private String lastMessage;
    private String chatRoomId;
    private boolean unread;
}
