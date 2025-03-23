package kr.yuns.chat.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.yuns.auth.data.entity.User;
import kr.yuns.chat.data.entity.ChatRoom;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    ChatRoom getChatRoomByRoomId(String roomId);
    Optional<ChatRoom> findChatRoomByRoomId(String roomId);
    List<ChatRoom> findChatRoomBySenderOrRecipient(User sender, User recipient);
}