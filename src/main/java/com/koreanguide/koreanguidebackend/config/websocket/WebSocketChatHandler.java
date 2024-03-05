package com.koreanguide.koreanguidebackend.config.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.koreanguide.koreanguidebackend.domain.auth.data.dao.UserDao;
import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.chat.data.dao.ChatDao;
import com.koreanguide.koreanguidebackend.domain.chat.data.dto.ChatMessageDto;
import com.koreanguide.koreanguidebackend.domain.chat.data.dto.response.ChatResponseDto;
import com.koreanguide.koreanguidebackend.domain.chat.data.entity.ChatMessage;
import com.koreanguide.koreanguidebackend.domain.chat.data.entity.ChatRoom;
import com.koreanguide.koreanguidebackend.domain.chat.data.enums.MessageType;
import com.koreanguide.koreanguidebackend.domain.track.data.dao.TrackDao;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.Track;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketChatHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private final Set<WebSocketSession> sessions = new HashSet<>();
    private final Map<String,Set<WebSocketSession>> chatRoomSessionMap = new HashMap<>();
    private final UserDao userDao;
    private final ChatDao chatDao;
    private final TrackDao trackDao;

    // 연결 후
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("{} 연결 됨", session.getId());
        sessions.add(session);
    }

    // 채팅 방 메시지 handle
    @Override
    @Transactional
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("payload {}", payload);

        ChatMessageDto chatMessageDto = objectMapper.readValue(payload, ChatMessageDto.class);
        log.info("session {}", chatMessageDto.toString());

        String chatRoomId = chatMessageDto.getChatRoomId();
        if(!chatRoomSessionMap.containsKey(chatRoomId)){
            chatRoomSessionMap.put(chatRoomId,new HashSet<>());
        }
        Set<WebSocketSession> chatRoomSession = chatRoomSessionMap.get(chatRoomId);

        if (chatMessageDto.getMessageType().equals(MessageType.ENTER)) {
            chatRoomSession.add(session);
            return;
        }
        if (chatRoomSession.size()>=3) {
            removeClosedSession(chatRoomSession);
        }

        User user = userDao.getUserEntity(chatMessageDto.getSenderId());
        ChatRoom chatRoom = chatDao.getChatRoomEntity(chatMessageDto.getChatRoomId());

        LocalDateTime CURRENT_TIME = LocalDateTime.now();

        chatDao.saveChatMessageEntity(ChatMessage.builder()
                    .user(user)
                    .chatRoom(chatRoom)
                    .message(chatMessageDto.getMessage())
                    .date(CURRENT_TIME)
                .build());

        ChatResponseDto chatResponseDto = new ChatResponseDto();
        chatResponseDto.setName(user.getNickname());
        chatResponseDto.setSenderId(user.getId());
        chatResponseDto.setMessage(chatMessageDto.getMessage());
        chatResponseDto.setDate(CURRENT_TIME);
        chatResponseDto.setProfileUrl(user.getProfileUrl());

        if(chatMessageDto.isUseFunction() && chatMessageDto.isUseTrackFunction()) {
            chatResponseDto.setUseTrackFunction(true);
            Track track = trackDao.getTrackEntity(chatMessageDto.getTargetTrackId());

            chatResponseDto.setTrackTitle(track.getTrackTitle());
            chatResponseDto.setTrackPrimaryUrl(track.getPrimaryImageUrl());
            chatResponseDto.setTrackId(track.getId());
            chatResponseDto.setTrackPreview(track.getTrackPreview());
        }

        sendMessageToChatRoom(chatResponseDto, chatRoomSession);
    }

    // 채팅 세션 연결이 끊긴 후
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("{} 연결 끊김", session.getId());
        sessions.remove(session);
    }

    // 세션 삭제
    private void removeClosedSession(Set<WebSocketSession> chatRoomSession) {
        chatRoomSession.removeIf(sess -> !sessions.contains(sess));
    }

    // 채팅 방에 메시지 전달
    private void sendMessageToChatRoom(ChatResponseDto chatResponseDto, Set<WebSocketSession> chatRoomSession) {
        chatRoomSession.parallelStream().forEach(sess -> sendMessage(sess, chatResponseDto));
    }

    // 메시지 전송
    public <T> void sendMessage(WebSocketSession session, T message) {
        try{
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

}
