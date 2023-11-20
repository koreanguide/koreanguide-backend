package com.koreanguide.koreanguidebackend.domain.chat.controller;

import com.koreanguide.koreanguidebackend.common.BaseResponseDto;
import com.koreanguide.koreanguidebackend.domain.chat.data.dto.CreateChatRoomRequestDto;
import com.koreanguide.koreanguidebackend.domain.chat.data.dto.response.ChatListResponseDto;
import com.koreanguide.koreanguidebackend.domain.chat.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {
    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/create")
    public ResponseEntity<BaseResponseDto> createChatRoom(@RequestBody CreateChatRoomRequestDto createChatRoomRequestDto) {
        return chatService.createChatRoom(createChatRoomRequestDto);
    }

    @GetMapping("/list")
    public ResponseEntity<List<ChatListResponseDto>> getChatList(@RequestParam Long userId) {
        return chatService.getChatList(userId);
    }
}
