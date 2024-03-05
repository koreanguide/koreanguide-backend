package com.koreanguide.koreanguidebackend.domain.chat.data.dao.Impl;

import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.chat.data.dao.ChatDao;
import com.koreanguide.koreanguidebackend.domain.chat.data.entity.ChatMessage;
import com.koreanguide.koreanguidebackend.domain.chat.data.entity.ChatRoom;
import com.koreanguide.koreanguidebackend.domain.chat.data.repository.ChatMessageRepository;
import com.koreanguide.koreanguidebackend.domain.chat.data.repository.ChatRoomRepository;
import com.koreanguide.koreanguidebackend.domain.chat.exception.ChatRoomNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ChatDaoImpl implements ChatDao {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;

    public ChatDaoImpl(ChatMessageRepository chatMessageRepository, ChatRoomRepository chatRoomRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatRoomRepository = chatRoomRepository;
    }

    @Override
    public void saveChatRoomEntity(ChatRoom chatRoom) {
        chatRoomRepository.save(chatRoom);
    }

    @Override
    public void saveChatMessageEntity(ChatMessage chatMessage) {
        chatMessageRepository.save(chatMessage);
    }

    @Override
    public List<ChatRoom> findUserChatRoomEntity(User user) {
        return chatRoomRepository.findChatRoomBySenderOrRecipient(user, user);
    }

    @Override
    public ChatRoom getChatRoomEntity(String chatRoomId) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findChatRoomByRoomId(chatRoomId);

        if(chatRoom.isEmpty()) {
            throw new ChatRoomNotFoundException();
        }

        return chatRoom.get();
    }

    @Override
    public List<ChatMessage> getChatMessageEntity(ChatRoom chatRoom) {
        return chatMessageRepository.findAllByChatRoom(chatRoom);
    }

    @Override
    public Page<ChatMessage> getChatMessageEntityViaPageable(ChatRoom chatRoom, Pageable pageable) {
        return chatMessageRepository.findAllByChatRoomOrderByDateDesc(chatRoom, pageable);
    }
}
