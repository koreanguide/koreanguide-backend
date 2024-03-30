package com.koreanguide.koreanguidebackend.domain.appointment.data.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppointmentMainResponseDto {
    private Long appointmentId;
    private String uuid;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private String targetNickname;
    private String targetProfileUrl;
    private Long trackId;
    private String trackName;
    private Long credit;
    private Long depositPercent;
    private Long totalCredit;
    private String fullAddress;
    private String kakaoMapUrl;
    private String targetUserEmail;
    private String status;
    private String createAt;
    private String chatRoomId;
    private boolean done;
    private boolean cancel;
}
