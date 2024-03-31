package com.koreanguide.koreanguidebackend.domain.appointment.service.Impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.koreanguide.koreanguidebackend.domain.appointment.data.dao.AppointmentDao;
import com.koreanguide.koreanguidebackend.domain.appointment.data.dto.entity.Appointment;
import com.koreanguide.koreanguidebackend.domain.appointment.data.dto.response.AppointmentMainResponseDto;
import com.koreanguide.koreanguidebackend.domain.appointment.data.dto.response.AppointmentReceiptResponseDto;
import com.koreanguide.koreanguidebackend.domain.appointment.data.enums.AppointmentStatus;
import com.koreanguide.koreanguidebackend.domain.appointment.service.AppointmentService;
import com.koreanguide.koreanguidebackend.domain.auth.data.dao.UserDao;
import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.auth.data.enums.UserType;
import com.koreanguide.koreanguidebackend.domain.chat.data.dao.ChatDao;
import com.koreanguide.koreanguidebackend.domain.track.data.dao.TrackDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentDao appointmentDao;
    private final UserDao userDao;
    private final TrackDao trackDao;
    private final ChatDao chatDao;

    @Value("${KAKAO.CLIENT.ID}")
    private String REST_API_KEY;

    // AAAA년 BB월 CC일 D요일 형식 변환
    public String FORMAT_DATE_TO_STRING_YMDE(LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 E요일");
        return time.format(formatter);
    }

    // 오전 or 오후 A시 형식 변환
    public String FORMAT_DATE_TO_STRING_HM(LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("a KK시");
        return time.format(formatter);
    }

    // AAAA년 BB월 CC일 D요일 오전 or 오후 E시 형식 변환
    public String FORMAT_DATE_TO_STRING_YMDEHM(LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 E요일 a KK시");
        return time.format(formatter);
    }

    public String getAddress(double longitude, double latitude) {
        try {
            String urlString = "https://dapi.kakao.com/v2/local/geo/coord2address.json?x=" + longitude
                    + "&y=" + latitude;
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "KakaoAK " + REST_API_KEY);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response.toString());
            JsonNode documents = rootNode.path("documents");
            JsonNode roadAddress = documents.get(0).path("address");

            System.out.println(roadAddress.path("address_name").asText());
            return roadAddress.path("address_name").asText();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return "주소를 가져올 수 없음";
    }

    @Override
    public ResponseEntity<?> requestCancelAppointment(Long userId, Long appointmentId) {
        User user = userDao.getUserEntity(userId);

        Appointment appointment = appointmentDao.getAppointmentEntity(appointmentId);

        if(!appointment.getGuide().equals(user) || !appointment.getVisitor().equals(user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자의 일정이 아닙니다.");
        }

        if(appointment.getRequestCancelUserType() == null || appointment.isCanceled() ||
        appointment.getAppointmentStatus().equals(AppointmentStatus.PENDING_CANCEL) ||
                appointment.getAppointmentStatus().equals(AppointmentStatus.CANCELED_WITH_ACCEPTED)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 취소되었거나 취소 요청 중인 일정입니다.");
        }

        appointment.setRequestCancelUserType(UserType.DOMESTIC);
        appointment.setRequestCancelAt(LocalDateTime.now());
        appointment.setAppointmentStatus(AppointmentStatus.PENDING_CANCEL);

        appointmentDao.saveAppointmentEntity(appointment);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<List<AppointmentMainResponseDto>> getAppointmentInfoUsedByMain(Long userId) {
        User user = userDao.getUserEntity(userId);
        List<Appointment> appointmentList = appointmentDao.getAppointmentListByUser(user);
        List<AppointmentMainResponseDto> appointmentMainResponseDtoList = new ArrayList<>();

        for(Appointment appointment : appointmentList) {
            AppointmentMainResponseDto appointmentMainResponseDto = new AppointmentMainResponseDto();
            appointmentMainResponseDto.setAppointmentId(appointment.getId());
            appointmentMainResponseDto.setUuid(appointment.getUuid());

            appointmentMainResponseDto.setStartDate(
                    FORMAT_DATE_TO_STRING_YMDE(appointment.getStartAt())
            );
            appointmentMainResponseDto.setEndDate(
                    FORMAT_DATE_TO_STRING_YMDE(appointment.getEndAt())
            );
            appointmentMainResponseDto.setStartTime(
                    FORMAT_DATE_TO_STRING_HM(appointment.getStartAt())
            );
            appointmentMainResponseDto.setEndTime(
                    FORMAT_DATE_TO_STRING_HM(appointment.getEndAt())
            );

            appointmentMainResponseDto.setTargetNickname(appointment.getVisitor().getNickname());
            appointmentMainResponseDto.setTargetProfileUrl(appointment.getVisitor().getProfileUrl());
            appointmentMainResponseDto.setTrackId(appointment.getTrack().getId());
            appointmentMainResponseDto.setTrackName(appointment.getTrack().getTrackTitle());
            appointmentMainResponseDto.setCredit(appointment.getCredit());
            appointmentMainResponseDto.setDepositPercent(appointment.getDepositPercent());
            appointmentMainResponseDto.setTotalCredit(appointment.getCredit() +
                    appointment.getCredit() * appointment.getDepositPercent() / 100);

            String KAKAO_MAP_URL = "https://map.kakao.com/link/map/" + appointment.getLatitude() + ","
                    + appointment.getLongitude();
            appointmentMainResponseDto.setKakaoMapUrl(KAKAO_MAP_URL);
            String FULL_ADDRESS = getAddress(appointment.getLongitude(), appointment.getLatitude()) +
                    " " + appointment.getAddressDetail();
            appointmentMainResponseDto.setFullAddress(FULL_ADDRESS);

            appointmentMainResponseDto.setTargetUserEmail(appointment.getVisitor().getEmail());

            String STATUS_STRING;
            switch (appointment.getAppointmentStatus()) {
                case PENDING_CANCEL:
                    STATUS_STRING = "약속 취소 요청 대기 중";
                    appointmentMainResponseDto.setDone(false);
                    appointmentMainResponseDto.setCancel(false);
                case CANCELED_WITH_ACCEPTED:
                    if(appointment.getRequestCancelUserType().equals(UserType.DOMESTIC)) {
                        STATUS_STRING = "취소 됨, 본인 요청(" + FORMAT_DATE_TO_STRING_YMDE(appointment.getCanceledAcceptAt()) + ")";
                    } else {
                        STATUS_STRING = "취소 됨, 타인 요청(" + FORMAT_DATE_TO_STRING_YMDE(appointment.getCanceledAcceptAt()) + ")";
                    }
                    appointmentMainResponseDto.setDone(false);
                    appointmentMainResponseDto.setCancel(true);
                case WAITING_OFFLINE_MEETING:
                    appointmentMainResponseDto.setDone(false);
                    appointmentMainResponseDto.setCancel(false);
                    STATUS_STRING = "오프라인 만남 대기 중";
                case DONE:
                    appointmentMainResponseDto.setDone(true);
                    appointmentMainResponseDto.setCancel(false);
                    STATUS_STRING = "완료됨";
                default:
                    appointmentMainResponseDto.setDone(false);
                    appointmentMainResponseDto.setCancel(false);
                    STATUS_STRING = "상태를 조회할 수 없음";
            }

            appointmentMainResponseDto.setStatus(STATUS_STRING);

            appointmentMainResponseDto.setCreateAt(FORMAT_DATE_TO_STRING_YMDE(appointment.getCreateAt()));
            appointmentMainResponseDto.setChatRoomId(appointment.getChatRoom().getRoomId());

            appointmentMainResponseDtoList.add(appointmentMainResponseDto);
        }

        return ResponseEntity.status(HttpStatus.OK).body(appointmentMainResponseDtoList);
    }

    @Override
    public ResponseEntity<AppointmentReceiptResponseDto> getAppointmentReceiptInfo(Long userId, Long appointmentId) {
        User user = userDao.getUserEntity(userId);
        AppointmentReceiptResponseDto appointmentReceiptResponseDto = new AppointmentReceiptResponseDto();
        Appointment appointment = appointmentDao.getAppointmentEntity(appointmentId);

        if(!appointment.getGuide().equals(user) || !appointment.getVisitor().equals(user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        appointmentReceiptResponseDto.setUuid(appointment.getUuid());
        appointmentReceiptResponseDto.setCreateAt(
                FORMAT_DATE_TO_STRING_YMDEHM(appointment.getCreateAt())
        );
        appointmentReceiptResponseDto.setModifyAt(
                FORMAT_DATE_TO_STRING_YMDEHM(appointment.getModifyAt())
        );
        appointmentReceiptResponseDto.setStartDate(
                FORMAT_DATE_TO_STRING_YMDE(appointment.getStartAt())
        );
        appointmentReceiptResponseDto.setEndDate(
                FORMAT_DATE_TO_STRING_YMDE(appointment.getEndAt())
        );
        appointmentReceiptResponseDto.setStartTime(
                FORMAT_DATE_TO_STRING_HM(appointment.getStartAt())
        );
        appointmentReceiptResponseDto.setEndTime(
                FORMAT_DATE_TO_STRING_HM(appointment.getEndAt())
        );
        appointmentReceiptResponseDto.setRequestUserProfileUrl(appointment.getGuide().getProfileUrl());
        appointmentReceiptResponseDto.setRequestUserNickname(appointment.getGuide().getNickname());
        appointmentReceiptResponseDto.setTargetUserProfileUrl(appointment.getVisitor().getProfileUrl());
        appointmentReceiptResponseDto.setTargetUserNickname(appointment.getVisitor().getNickname());

        if(appointment.getRequestCancelAt() != null) {
            appointmentReceiptResponseDto.setCancelRequestExist(true);
            appointmentReceiptResponseDto.setCancelRequestAt(
                    FORMAT_DATE_TO_STRING_YMDEHM(appointment.getRequestCancelAt())
            );

            switch (appointment.getRequestCancelUserType()) {
                case DOMESTIC:
                    appointmentReceiptResponseDto.setCancelRequestUserProfileUrl(
                            appointment.getGuide().getProfileUrl()
                    );
                    appointmentReceiptResponseDto.setCancelRequestUserNickname(
                            appointment.getGuide().getNickname()
                    );
                case FOREIGNTER:
                    appointmentReceiptResponseDto.setCancelRequestUserProfileUrl(
                            appointment.getVisitor().getProfileUrl()
                    );
                    appointmentReceiptResponseDto.setCancelRequestUserNickname(
                            appointment.getVisitor().getNickname()
                    );
            }
        } else {
            appointmentReceiptResponseDto.setCancelRequestExist(false);
            appointmentReceiptResponseDto.setCancelRequestUserProfileUrl(null);
            appointmentReceiptResponseDto.setCancelRequestUserNickname(null);
            appointmentReceiptResponseDto.setCancelRequestAt(null);
        }

        appointmentReceiptResponseDto.setAcceptAt(
                FORMAT_DATE_TO_STRING_YMDEHM(appointment.getAcceptAt())
        );

        appointmentReceiptResponseDto.setAirlineInfo(appointment.getAirlineInfo());



        return ResponseEntity.status(HttpStatus.OK).body(appointmentReceiptResponseDto);
    }

    @Override
    public void createTestAppointment(Long userId) {
        appointmentDao.saveAppointmentEntity(Appointment.builder()
                        .uuid("1E31FD")
                        .createAt(LocalDateTime.now())
                        .startAt(LocalDateTime.now())
                        .endAt(LocalDateTime.now())
                        .modifyAt(LocalDateTime.now())
                        .acceptAt(LocalDateTime.now())
                        .isAccept(true)
                        .airlineInfo("K123")
                        .track(trackDao.getTrackEntity(4L))
                        .credit(190000L)
                        .depositPercent(20L)
                        .depositCredit((long) (190000 + 190000 * 20 / 100))
                        .chatRoom(chatDao.getChatRoomEntity("722f4d64-4ed7-45ad-9cbe-09efc3a76258"))
                        .latitude(37.555946)
                        .longitude(126.972317)
                        .addressDetail("1번출구 앞")
                        .appointmentStatus(AppointmentStatus.WAITING_OFFLINE_MEETING)
                        .guide(userDao.getUserEntity(userId))
                        .visitor(userDao.getUserEntity(2L))
                .build());
    }
}
