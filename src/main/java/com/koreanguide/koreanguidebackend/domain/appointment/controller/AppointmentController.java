package com.koreanguide.koreanguidebackend.domain.appointment.controller;

import com.koreanguide.koreanguidebackend.domain.appointment.data.dto.response.AppointmentMainResponseDto;
import com.koreanguide.koreanguidebackend.domain.appointment.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/appointment")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;

    @GetMapping("/")
    public ResponseEntity<List<AppointmentMainResponseDto>> getAppointmentInfoUsedByMain(Long userId) {
        return appointmentService.getAppointmentInfoUsedByMain(userId);
    }
}
