package kr.yuns.chat.service;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import kr.yuns.chat.data.dto.ChatListResponseDto;
import kr.yuns.chat.data.dto.ChatResponseDto;
import kr.yuns.chat.data.dto.CreateChatRoomRequestDto;

import java.util.List;

public interface ChatService {
    ResponseEntity<?> createChatRoom(CreateChatRoomRequestDto createChatRoomRequestDto);
    ResponseEntity<List<ChatResponseDto>> getAllChattingList(String chatRoomId);
    ResponseEntity<List<ChatListResponseDto>> getChatList(Long userId);
    ResponseEntity<List<ChatResponseDto>> getChatMsg(String roomId, Pageable pageable);
}