package kr.yuns.chat.data.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.yuns.auth.data.entity.User;
import kr.yuns.chat.data.entity.ChatMessage;
import kr.yuns.chat.data.entity.ChatRoom;

import java.util.List;

public interface ChatDao {
    void saveChatRoomEntity(ChatRoom chatRoom);
    void saveChatMessageEntity(ChatMessage chatMessage);
    List<ChatRoom> findUserChatRoomEntity(User user);
    ChatRoom getChatRoomEntity(String chatRoomId);
    List<ChatMessage> getChatMessageEntity(ChatRoom chatRoom);
    Page<ChatMessage> getChatMessageEntityViaPageable(ChatRoom chatRoom, Pageable pageable);
}