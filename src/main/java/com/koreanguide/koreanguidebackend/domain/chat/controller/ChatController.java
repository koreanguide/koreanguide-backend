package com.koreanguide.koreanguidebackend.domain.chat.controller;

import com.koreanguide.koreanguidebackend.config.security.JwtTokenProvider;
import com.koreanguide.koreanguidebackend.domain.chat.data.dto.CreateChatRoomRequestDto;
import com.koreanguide.koreanguidebackend.domain.chat.data.dto.response.ChatListResponseDto;
import com.koreanguide.koreanguidebackend.domain.chat.data.dto.response.ChatResponseDto;
import com.koreanguide.koreanguidebackend.domain.chat.service.ChatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(tags = {"Chat API"})
@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {
    private final ChatService chatService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public ChatController(ChatService chatService, JwtTokenProvider jwtTokenProvider) {
        this.chatService = chatService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @ApiOperation(value = "채팅방 생성")
    @PostMapping("/create")
    public ResponseEntity<?> createChatRoom(@RequestBody CreateChatRoomRequestDto createChatRoomRequestDto) {
        return chatService.createChatRoom(createChatRoomRequestDto);
    }

    @ApiOperation(value = "사용자 별 채팅방 리스트 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    @GetMapping("/list")
    public ResponseEntity<List<ChatListResponseDto>> getChatList(HttpServletRequest request) {
        return chatService.getChatList(jwtTokenProvider.getUserIdByToken(request.getHeader("X-AUTH-TOKEN")));
    }

    @ApiOperation(value = "채팅 메시지 조회(미사용)")
    @GetMapping("/")
    public ResponseEntity<List<ChatResponseDto>> getChatMsg(@RequestParam String roomId, @PageableDefault(size = 10) Pageable pageable) {
        return chatService.getChatMsg(roomId, pageable);
    }

    @ApiOperation(value = "채팅 메시지 조회")
    @GetMapping("/msg")
    public ResponseEntity<List<ChatResponseDto>> getAllChattingMsg(@RequestParam String chatRoomId) {
        return chatService.getAllChattingList(chatRoomId);
    }
}
