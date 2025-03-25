package kr.yuns.chat.data.dao.Impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import kr.yuns.auth.data.entity.User;
import kr.yuns.chat.data.dao.ChatDao;
import kr.yuns.chat.data.entity.ChatMessage;
import kr.yuns.chat.data.entity.ChatRoom;
import kr.yuns.chat.data.repository.ChatMessageRepository;
import kr.yuns.chat.data.repository.ChatRoomRepository;
import kr.yuns.chat.exception.ChatRoomNotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ChatDaoImpl implements ChatDao {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;

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