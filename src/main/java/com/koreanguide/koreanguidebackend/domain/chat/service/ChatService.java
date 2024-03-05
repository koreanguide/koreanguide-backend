package com.koreanguide.koreanguidebackend.domain.chat.service;

import com.koreanguide.koreanguidebackend.domain.chat.data.dto.CreateChatRoomRequestDto;
import com.koreanguide.koreanguidebackend.domain.chat.data.dto.response.ChatListResponseDto;
import com.koreanguide.koreanguidebackend.domain.chat.data.dto.response.ChatResponseDto;
import com.koreanguide.koreanguidebackend.domain.chat.data.entity.ChatMessage;
import com.koreanguide.koreanguidebackend.domain.chat.data.entity.ChatRoom;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ChatService {
    void saveChatRoom(ChatRoom chatRoom);
    ResponseEntity<?> createChatRoom(CreateChatRoomRequestDto createChatRoomRequestDto);
    ResponseEntity<List<ChatResponseDto>> getAllChattingList(String chatRoomId);
    ResponseEntity<List<ChatListResponseDto>> getChatList(Long userId);
    ResponseEntity<List<ChatResponseDto>> getChatMsg(String roomId, Pageable pageable);
    void saveChatMessage(ChatMessage chatMessage);
}
