package com.koreanguide.koreanguidebackend.domain.appointment.data.entity;

import com.koreanguide.koreanguidebackend.domain.appointment.data.enums.AppointmentStatus;
import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.auth.data.enums.UserType;
import com.koreanguide.koreanguidebackend.domain.chat.data.entity.ChatRoom;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.Track;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String uuid;

    // 약속 생성일
    @Column(nullable = false)
    private LocalDateTime createAt;

    // 약속 시작일
    @Column(nullable = false)
    private LocalDateTime startAt;

    // 약속 종료일
    @Column(nullable = false)
    private LocalDateTime endAt;

    // 약속 수정일
    @Column(nullable = false)
    private LocalDateTime modifyAt;

    // 취소 요청 사용자 구분
    @Column
    @Enumerated(EnumType.STRING)
    private UserType requestCancelUserType;

    // 취소 요청일
    @Column
    private LocalDateTime requestCancelAt;

    @Column
    private LocalDateTime canceledAcceptAt;

    // 취소 수락 여부
    @Column
    private boolean isCanceled;

    // 약속 수락일
    @Column(nullable = false)
    private LocalDateTime acceptAt;

    // 약속 수락 여부
    @Column(nullable = false)
    private boolean isAccept;

    // 항공편 정보
    @Column(nullable = false)
    private String airlineInfo;

    // 등록 트랙 정보
    @ManyToOne
    @JoinColumn(name = "track_id")
    private Track track;

    @Column(nullable = false)
    private Long credit;

    @Column(nullable = false)
    private Long depositPercent;

    @Column(nullable = false)
    private Long depositCredit;

    @ManyToOne
    @JoinColumn(name = "chatRoom_id")
    private ChatRoom chatRoom;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column
    private String addressDetail;

    // 진행 상태
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AppointmentStatus appointmentStatus;

    @ManyToOne
    @JoinColumn(name = "guide_id")
    private User guide;

    @ManyToOne
    @JoinColumn(name = "visitor_id")
    private User visitor;
}
