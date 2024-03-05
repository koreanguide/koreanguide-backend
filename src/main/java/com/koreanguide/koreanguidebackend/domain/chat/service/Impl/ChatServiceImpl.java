package com.koreanguide.koreanguidebackend.domain.chat.service.Impl;

import com.koreanguide.koreanguidebackend.domain.auth.data.dao.UserDao;
import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.chat.data.dao.ChatDao;
import com.koreanguide.koreanguidebackend.domain.chat.data.dto.CreateChatRoomRequestDto;
import com.koreanguide.koreanguidebackend.domain.chat.data.dto.response.ChatListResponseDto;
import com.koreanguide.koreanguidebackend.domain.chat.data.dto.response.ChatResponseDto;
import com.koreanguide.koreanguidebackend.domain.chat.data.entity.ChatMessage;
import com.koreanguide.koreanguidebackend.domain.chat.data.entity.ChatRoom;
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
import java.util.UUID;

@Service
public class ChatServiceImpl implements ChatService {
    private final UserDao userDao;
    private final ChatDao chatDao;

    @Autowired
    public ChatServiceImpl(UserDao userDao, ChatDao chatDao) {
        this.userDao = userDao;
        this.chatDao = chatDao;
    }

    @Override
    public ResponseEntity<?> createChatRoom(CreateChatRoomRequestDto createChatRoomRequestDto) {
        User sender = userDao.getUserEntity(createChatRoomRequestDto.getSenderId());
        User recipient = userDao.getUserEntity(createChatRoomRequestDto.getRecipientId());

        String CHAT_ROOM_ID = UUID.randomUUID().toString();
        chatDao.saveChatRoomEntity(ChatRoom.builder()
                .roomId(CHAT_ROOM_ID)
                .createdAt(LocalDateTime.now())
                .sender(sender)
                .recipient(recipient)
                .build());

        return ResponseEntity.status(HttpStatus.OK).body(CHAT_ROOM_ID);
    }

    @Override
    public ResponseEntity<List<ChatResponseDto>> getAllChattingList(String chatRoomId) {
        ChatRoom chatRoom = chatDao.getChatRoomEntity(chatRoomId);
        List<ChatMessage> chatMessageList = chatDao.getChatMessageEntity(chatRoom);
        List<ChatResponseDto> chatResponseDtoList = new ArrayList<>();

        for(ChatMessage chatMessage : chatMessageList) {
            ChatResponseDto chatResponseDto = new ChatResponseDto();
            chatResponseDto.setProfileUrl(chatMessage.getUser().getProfileUrl());
            chatResponseDto.setName(chatMessage.getUser().getNickname());
            chatResponseDto.setMessage(chatMessage.getMessage());
            chatResponseDto.setDate(chatMessage.getDate());
            chatResponseDto.setSenderId(chatMessage.getUser().getId());

            chatResponseDtoList.add(chatResponseDto);
        }

        return ResponseEntity.status(HttpStatus.OK).body(chatResponseDtoList);
    }

    @Override
    public ResponseEntity<List<ChatListResponseDto>> getChatList(Long userId) {
        User user = userDao.getUserEntity(userId);

        List<ChatRoom> chatRoomList = chatDao.findUserChatRoomEntity(user);
        List<ChatListResponseDto> chatListResponseDtoList = new ArrayList<>();

        for(ChatRoom chatRoom : chatRoomList) {
            List<ChatMessage> chatMessageList = chatDao.getChatMessageEntity(chatRoom);
            User targetUser = chatRoom.getSender() != chatRoom.getSender() ? user : chatRoom.getRecipient();
            ChatListResponseDto chatListResponseDto = new ChatListResponseDto();
            chatListResponseDto.setName(targetUser.getNickname());
            if(chatMessageList.isEmpty()) {
                chatListResponseDto.setLastTalkedAt("알 수 없음");
                chatListResponseDto.setLastMessage("대화를 시작했습니다.");
            } else {
                chatListResponseDto.setLastTalkedAt(chatMessageList.get(chatMessageList.size() - 1).getDate().toString());
                chatListResponseDto.setLastMessage(chatMessageList.get(chatMessageList.size() - 1).getMessage());
            }
            chatListResponseDto.setChatRoomId(chatRoom.getRoomId());
            chatListResponseDto.setProfileUrl(targetUser.getProfileUrl());
            chatListResponseDto.setUnread(false);

            chatListResponseDtoList.add(chatListResponseDto);
        }

        return ResponseEntity.status(HttpStatus.OK).body(chatListResponseDtoList);
    }

    @Override
    public ResponseEntity<List<ChatResponseDto>> getChatMsg(String roomId, Pageable pageable) {
        ChatRoom chatRoom = chatDao.getChatRoomEntity(roomId);
        Page<ChatMessage> chatMessages = chatDao.getChatMessageEntityViaPageable(chatRoom, pageable);
        List<ChatResponseDto> chatResponseDtoList = new ArrayList<>();

        for(ChatMessage chatMessage : chatMessages.getContent()) {
            chatResponseDtoList.add(ChatResponseDto.builder()
                    .name(chatMessage.getUser().getNickname())
                    .date(chatMessage.getDate())
                    .message(chatMessage.getMessage())
                    .profileUrl(null)
                    .build());
        }

        return ResponseEntity.status(HttpStatus.OK).body(chatResponseDtoList);
    }
}
