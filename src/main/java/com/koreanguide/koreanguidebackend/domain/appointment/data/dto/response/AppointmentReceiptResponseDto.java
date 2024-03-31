package com.koreanguide.koreanguidebackend.domain.appointment.data.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppointmentReceiptResponseDto {
    private String uuid;
    private String createAt;
    private String modifyAt;
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;
    private String requestUserProfileUrl;
    private String requestUserNickname;
    private String targetUserProfileUrl;
    private String targetUserNickname;
    private boolean cancelRequestExist;
    private String cancelRequestUserProfileUrl;
    private String cancelRequestUserNickname;
    private String cancelRequestAt;
    private String acceptAt;
    private String airlineInfo;
    private String targetTrackName;
    private String fullAddress;
    private String kakaoMapUrl;
    private String meetAt;
    private Long credit;
    private Long depositPercent;
    private Long depositCredit;
}
