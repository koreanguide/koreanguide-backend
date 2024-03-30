package com.koreanguide.koreanguidebackend.domain.appointment.service;

import com.koreanguide.koreanguidebackend.domain.appointment.data.dto.response.AppointmentMainResponseDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AppointmentService {
    ResponseEntity<?> requestCancelAppointment(Long userId, Long appointmentId);

    ResponseEntity<List<AppointmentMainResponseDto>> getAppointmentInfoUsedByMain(Long userId);
}
