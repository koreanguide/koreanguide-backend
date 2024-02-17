package com.koreanguide.koreanguidebackend.domain.chat.data.dto;

import com.koreanguide.koreanguidebackend.domain.chat.data.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageDto {
//    메시지 분류
    private MessageType messageType;
//    채팅방 ID
    private String chatRoomId;
//    전송자 ID
    private Long senderId;
//    메시지
    private String message;
//    전체 기능 사용 여부
    private boolean useFunction;
//    트랙 전송 기능 사용 여부
    private boolean useTrackFunction;
//    트랙 고유 ID 값
    private Long targetTrackId;
//    약속 확정 요청 기능 사용 여부
    private boolean useAppointmentFunction;
//    약속 고유 ID 값
    private Long targetAppointmentId;
//    약속 취소 요청 기능 사용 여부
    private boolean useCancelAppointmentFunction;
}
