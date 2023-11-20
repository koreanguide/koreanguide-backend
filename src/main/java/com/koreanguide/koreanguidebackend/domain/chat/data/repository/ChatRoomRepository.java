package com.koreanguide.koreanguidebackend.domain.chat.data.repository;

import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.chat.data.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    ChatRoom getChatRoomByRoomId(String roomId);
    Optional<ChatRoom> findChatRoomByRoomId(String roomId);
    List<ChatRoom> findChatRoomBySenderOrRecipient(User sender, User recipient);
}
