package com.koreanguide.koreanguidebackend.domain.chat.service;

import com.koreanguide.koreanguidebackend.common.BaseResponseDto;
import com.koreanguide.koreanguidebackend.domain.chat.data.dto.CreateChatRoomRequestDto;
import com.koreanguide.koreanguidebackend.domain.chat.data.dto.response.ChatListResponseDto;
import com.koreanguide.koreanguidebackend.domain.chat.data.entity.ChatMessage;
import com.koreanguide.koreanguidebackend.domain.chat.data.entity.ChatRoom;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ChatService {
    void saveChatRoom(ChatRoom chatRoom);

    ResponseEntity<BaseResponseDto> createChatRoom(CreateChatRoomRequestDto createChatRoomRequestDto);

    ResponseEntity<List<ChatListResponseDto>> getChatList(Long userId);

    void saveChatMessage(ChatMessage chatMessage);
}
