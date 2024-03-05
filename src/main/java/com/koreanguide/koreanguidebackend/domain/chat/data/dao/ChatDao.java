package com.koreanguide.koreanguidebackend.domain.chat.data.dao;

import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.chat.data.entity.ChatMessage;
import com.koreanguide.koreanguidebackend.domain.chat.data.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChatDao {
    void saveChatRoomEntity(ChatRoom chatRoom);

    void saveChatMessageEntity(ChatMessage chatMessage);

    List<ChatRoom> findUserChatRoomEntity(User user);

    ChatRoom getChatRoomEntity(String chatRoomId);

    List<ChatMessage> getChatMessageEntity(ChatRoom chatRoom);

    Page<ChatMessage> getChatMessageEntityViaPageable(ChatRoom chatRoom, Pageable pageable);
}
