package com.koreanguide.koreanguidebackend.domain.chat.data.dto;

import com.koreanguide.koreanguidebackend.domain.chat.data.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageDto {
    private MessageType messageType;
    private String chatRoomId;
    private Long senderId;
    private String message;
}
