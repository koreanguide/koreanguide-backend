package kr.yuns.chat.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import kr.yuns.chat.data.entity.ChatMessage;
import kr.yuns.chat.data.entity.ChatRoom;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findAllByChatRoom(ChatRoom chatRoom);
    Page<ChatMessage> findAllByChatRoomOrderByDateDesc(ChatRoom chatRoom, Pageable pageable);
}