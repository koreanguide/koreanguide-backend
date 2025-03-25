package kr.yuns.chat.handler;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import kr.yuns.JwtTokenProvider;
import kr.yuns.auth.data.dao.UserDao;
import kr.yuns.auth.data.entity.User;
import kr.yuns.chat.data.dao.ChatDao;
import kr.yuns.chat.data.dto.ChatMessageDto;
import kr.yuns.chat.data.dto.ChatResponseDto;
import kr.yuns.chat.data.entity.ChatMessage;
import kr.yuns.chat.data.entity.ChatRoom;
import kr.yuns.chat.data.enums.MessageType;
import kr.yuns.track.data.dao.TrackDao;
import kr.yuns.track.data.entity.Track;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
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
public class WebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private final Set<WebSocketSession> sessions = new HashSet<>();
    private final Map<String,Set<WebSocketSession>> chatRoomSessionMap = new HashMap<>();
    private final UserDao userDao;
    private final ChatDao chatDao;
    private final TrackDao trackDao;
    private final JwtTokenProvider tokenProvider;

    public User GET_USER_ENTITY_BY_TOKEN(String token) {
        return userDao.getUserEntityByEmail(tokenProvider.getUserEmailByToken(token));
    }

    @Override
    public void afterConnectionEstablished(@SuppressWarnings("null") WebSocketSession session) {
        log.info("[WebSocket] Session connected, Session ID: {}", session.getId());
        sessions.add(session);
    }

    @Override
    @Transactional
    public void handleTextMessage(@SuppressWarnings("null") WebSocketSession session, @SuppressWarnings("null") TextMessage message) throws Exception {
        String payload = message.getPayload();

        ChatMessageDto chatMessageDto = objectMapper.readValue(payload, ChatMessageDto.class);
        log.info("[WebSocket] Session connected, Session ID: {}", chatMessageDto.toString());

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

        User user = GET_USER_ENTITY_BY_TOKEN(session.getHandshakeHeaders().getFirst("Authorization"));
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

    @Override
    public void afterConnectionClosed(@SuppressWarnings("null") WebSocketSession session, @SuppressWarnings("null") CloseStatus status) {
        log.info("[WebSocket] Session disconnected, Session ID: {}", session.getId());
        sessions.remove(session);
    }

    private void removeClosedSession(Set<WebSocketSession> chatRoomSession) {
        chatRoomSession.removeIf(sess -> !sessions.contains(sess));
    }

    private void sendMessageToChatRoom(ChatResponseDto chatResponseDto, Set<WebSocketSession> chatRoomSession) {
        chatRoomSession.parallelStream().forEach(sess -> sendMessage(sess, chatResponseDto));
    }

    public <T> void sendMessage(WebSocketSession session, T message) {
        try{
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}