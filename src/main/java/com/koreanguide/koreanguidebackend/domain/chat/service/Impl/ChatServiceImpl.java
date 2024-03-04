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
import com.koreanguide.koreanguidebackend.domain.profile.data.entity.Profile;
import com.koreanguide.koreanguidebackend.domain.profile.service.Impl.ProfileServiceImpl;
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
    private final ProfileServiceImpl profileService;

    @Autowired
    public ChatServiceImpl(ChatRoomRepository chatRoomRepository, ChatMessageRepository chatMessageRepository,
                           UserRepository userRepository, ProfileServiceImpl profileService) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
        this.profileService = profileService;
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
    public ResponseEntity<List<ChatResponseDto>> getAllChattingList(String chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.getChatRoomByRoomId(chatRoomId);

        List<ChatMessage> chatMessageList = chatMessageRepository.findAllByChatRoom(chatRoom);
        List<ChatResponseDto> chatResponseDtoList = new ArrayList<>();

        for(ChatMessage chatMessage : chatMessageList) {
            ChatResponseDto chatResponseDto = new ChatResponseDto();
            Profile profile = profileService.GET_PROFILE_BY_USER_ID(chatMessage.getUser().getId());
            chatResponseDto.setProfileUrl(profile.getProfileUrl());
            chatResponseDto.setName(profile.getUser().getNickname());
            chatResponseDto.setMessage(chatMessage.getMessage());
            chatResponseDto.setDate(chatMessage.getDate());
            chatResponseDto.setSenderId(chatMessage.getUser().getId());

            chatResponseDtoList.add(chatResponseDto);
        }

        return ResponseEntity.status(HttpStatus.OK).body(chatResponseDtoList);
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
            User targetUser = chatRoom.getSender() != chatRoom.getSender() ? user.get() : chatRoom.getRecipient();
            Profile profile = profileService.GET_PROFILE_BY_USER_ID(targetUser.getId());
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
            chatListResponseDto.setProfileUrl(profile.getProfileUrl());
            chatListResponseDto.setUnread(false);

            chatListResponseDtoList.add(chatListResponseDto);
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
                    .message(chatMessage.getMessage())
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
