package com.koreanguide.koreanguidebackend.domain.appointment.service.Impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.koreanguide.koreanguidebackend.domain.appointment.data.dao.AppointmentDao;
import com.koreanguide.koreanguidebackend.domain.appointment.data.dto.entity.Appointment;
import com.koreanguide.koreanguidebackend.domain.appointment.data.dto.response.AppointmentMainResponseDto;
import com.koreanguide.koreanguidebackend.domain.appointment.service.AppointmentService;
import com.koreanguide.koreanguidebackend.domain.auth.data.dao.UserDao;
import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.auth.data.enums.UserType;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentDao appointmentDao;
    private final UserDao userDao;

    @Value("${KAKAO.CLIENT.ID}")
    private String REST_API_KEY;

    public String getAddress(double longitude, double latitude) {
        try {
            String urlString = "https://dapi.kakao.com/v2/local/geo/coord2address.json?x=" + longitude
                    + "&y=" + latitude + "&input_coord=WGS84";
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
            JsonNode roadAddress = documents.get(0).path("road_address");

            return roadAddress.path("address_name").asText();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return "주소를 가져올 수 없음";
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

            DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 E요일");
            DateTimeFormatter formatTime = DateTimeFormatter.ofPattern("a KK시");

            appointmentMainResponseDto.setStartDate(appointment.getStartAt().format(formatDate));
            appointmentMainResponseDto.setEndDate(appointment.getEndAt().format(formatDate));
            appointmentMainResponseDto.setStartTime(appointment.getStartAt().format(formatTime));
            appointmentMainResponseDto.setEndTime(appointment.getEndAt().format(formatTime));
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
            if(appointment.getAddressDetail() != null) {
                appointmentMainResponseDto.setFullAddress(getAddress(appointment.getLongitude(),
                        appointment.getLatitude()) + appointment.getAddressDetail());
            } else {
                appointmentMainResponseDto.setFullAddress(getAddress(appointment.getLongitude(), appointment.getLatitude()));
            }

            appointmentMainResponseDto.setTargetUserEmail(appointment.getVisitor().getEmail());

            String STATUS_STRING;
            switch (appointment.getAppointmentStatus()) {
                case PENDING_CANCEL:
                    STATUS_STRING = "약속 취소 요청 대기 중";
                    appointmentMainResponseDto.setDone(false);
                    appointmentMainResponseDto.setCancel(false);
                case CANCELED_WITH_ACCEPTED:
                    if(appointment.getRequestCancelUserType().equals(UserType.DOMESTIC)) {
                        STATUS_STRING = "취소 됨, 본인 요청(" + appointment.getCanceledAcceptAt().format(formatDate) + ")";
                    } else {
                        STATUS_STRING = "취소 됨, 타인 요청(" + appointment.getCanceledAcceptAt().format(formatDate) + ")";
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

            appointmentMainResponseDto.setCreateAt(appointment.getCreateAt().format(formatDate));
            appointmentMainResponseDto.setChatRoomId(appointment.getChatRoom().getRoomId());

            appointmentMainResponseDtoList.add(appointmentMainResponseDto);
        }

        return ResponseEntity.status(HttpStatus.OK).body(appointmentMainResponseDtoList);
    }
}
