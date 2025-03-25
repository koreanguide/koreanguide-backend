package kr.yuns.chat;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import kr.yuns.JwtTokenProvider;
import kr.yuns.auth.data.dao.UserDao;
import kr.yuns.chat.data.dto.ChatListResponseDto;
import kr.yuns.chat.data.dto.ChatResponseDto;
import kr.yuns.chat.data.dto.CreateChatRoomRequestDto;
import kr.yuns.chat.service.ChatService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final JwtTokenProvider tokenProvider;
    private final UserDao userDao;

    public Long GET_USER_ID_BY_TOKEN(HttpServletRequest request) {
        return userDao.getUserId(tokenProvider.getUserEmail(request));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createChatRoom(@RequestBody CreateChatRoomRequestDto createChatRoomRequestDto) {
        return chatService.createChatRoom(createChatRoomRequestDto);
    }

    @GetMapping("/list")
    public ResponseEntity<List<ChatListResponseDto>> getChatList(HttpServletRequest request) {
        return chatService.getChatList(GET_USER_ID_BY_TOKEN(request));
    }

    @Deprecated
    @GetMapping("/")
    public ResponseEntity<List<ChatResponseDto>> getChatMsg(@RequestParam String roomId, @PageableDefault(size = 10) Pageable pageable) {
        return chatService.getChatMsg(roomId, pageable);
    }

    @GetMapping("/msg")
    public ResponseEntity<List<ChatResponseDto>> getAllChattingMsg(@RequestParam String chatRoomId) {
        return chatService.getAllChattingList(chatRoomId);
    }
}