package com.koreanguide.koreanguidebackend.domain.chat.service;

import com.koreanguide.koreanguidebackend.domain.chat.data.dto.CreateChatRoomRequestDto;
import com.koreanguide.koreanguidebackend.domain.chat.data.dto.response.ChatListResponseDto;
import com.koreanguide.koreanguidebackend.domain.chat.data.dto.response.ChatResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ChatService {
    ResponseEntity<?> createChatRoom(CreateChatRoomRequestDto createChatRoomRequestDto);
    ResponseEntity<List<ChatResponseDto>> getAllChattingList(String chatRoomId);
    ResponseEntity<List<ChatListResponseDto>> getChatList(Long userId);
    ResponseEntity<List<ChatResponseDto>> getChatMsg(String roomId, Pageable pageable);
}
