package com.koreanguide.koreanguidebackend.domain.chat.service.Impl;

import com.koreanguide.koreanguidebackend.common.BaseResponseDto;
import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.auth.data.repository.UserRepository;
import com.koreanguide.koreanguidebackend.domain.chat.data.dto.CreateChatRoomRequestDto;
import com.koreanguide.koreanguidebackend.domain.chat.data.dto.response.ChatListResponseDto;
import com.koreanguide.koreanguidebackend.domain.chat.data.dto.response.ChatResponseDto;
import com.koreanguide.koreanguidebackend.domain.chat.data.entity.ChatMessage;
import com.koreanguide.koreanguidebackend.domain.chat.data.entity.ChatRoom;
import com.koreanguide.koreanguidebackend.domain.chat.data.repository.ChatMessageRepository;
import com.koreanguide.koreanguidebackend.domain.chat.data.repository.ChatRoomRepository;
import com.koreanguide.koreanguidebackend.domain.chat.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ChatServiceImpl implements ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    @Autowired
    public ChatServiceImpl(ChatRoomRepository chatRoomRepository, ChatMessageRepository chatMessageRepository,
                           UserRepository userRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void saveChatRoom(ChatRoom chatRoom) {
        chatRoomRepository.save(chatRoom);
    }

    @Override
    public ResponseEntity<BaseResponseDto> createChatRoom(CreateChatRoomRequestDto createChatRoomRequestDto) {
        Optional<User> sender = userRepository.findById(createChatRoomRequestDto.getSenderId());
        Optional<User> recipient = userRepository.findById(createChatRoomRequestDto.getRecipientId());

        if(sender.isEmpty() || recipient.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BaseResponseDto.builder()
                            .success(false)
                            .msg("사용자가 존재하지 않습니다.")
                    .build());
        } else {
            String CHAT_ROOM_ID = UUID.randomUUID().toString();
            chatRoomRepository.save(ChatRoom.builder()
                    .roomId(CHAT_ROOM_ID)
                    .createdAt(LocalDateTime.now())
                    .sender(sender.get())
                    .recipient(recipient.get())
                    .build());

            return ResponseEntity.status(HttpStatus.OK).body(BaseResponseDto.builder()
                            .success(true)
                            .msg(CHAT_ROOM_ID)
                    .build());
        }
    }

    @Override
    public ResponseEntity<List<ChatListResponseDto>> getChatList(Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if(user.isEmpty()) {
            throw new RuntimeException("사용자를 찾을 수 없음");
        }

        List<ChatRoom> chatRoomList = chatRoomRepository.findChatRoomBySenderOrRecipient(user.get(), user.get());
        List<ChatListResponseDto> chatListResponseDtoList = new ArrayList<>();

        for(ChatRoom chatRoom : chatRoomList) {
            List<ChatMessage> chatMessageList = chatMessageRepository.findAllByChatRoom(chatRoom);
            User targetUser = chatRoom.getSender() != user.get() ? chatRoom.getRecipient() : chatRoom.getSender();
            chatListResponseDtoList.add(ChatListResponseDto.builder()
                            .lastTalkedAt(chatMessageList.get(chatMessageList.size() - 1).getDate().toString())
                            .name(targetUser.getNickname())
                            .lastMessage(chatMessageList.get(chatMessageList.size() - 1).getMessage())
                            // 상대방 프로필 사진
                            .profileUrl(null)
                            // 읽음 여부
                            .unread(false)
                    .build());
        }

        return ResponseEntity.status(HttpStatus.OK).body(chatListResponseDtoList);
    }

    @Override
    public ResponseEntity<List<ChatResponseDto>> getChatMsg(String roomId, Pageable pageable) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findChatRoomByRoomId(roomId);

        if(chatRoom.isEmpty()) {
            throw new RuntimeException("채팅방을 찾을 수 없음");
        }

        Page<ChatMessage> chatMessages = chatMessageRepository.findAllByChatRoomOrderByDateDesc(chatRoom.get(), pageable);
        List<ChatResponseDto> chatResponseDtoList = new ArrayList<>();

        for(ChatMessage chatMessage : chatMessages.getContent()) { // getContent() 메소드 사용
            chatResponseDtoList.add(ChatResponseDto.builder()
                    .name(chatMessage.getUser().getNickname())
                    .date(chatMessage.getDate())
                    .msg(chatMessage.getMessage())
                    .profileUrl(null)
                    .build());
        }

        return ResponseEntity.status(HttpStatus.OK).body(chatResponseDtoList);
    }

    @Override
    public void saveChatMessage(ChatMessage chatMessage) {
        chatMessageRepository.save(chatMessage);
    }
}
