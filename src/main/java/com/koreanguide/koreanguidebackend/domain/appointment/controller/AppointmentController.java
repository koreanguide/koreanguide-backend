package com.koreanguide.koreanguidebackend.domain.appointment.controller;

import com.koreanguide.koreanguidebackend.config.security.JwtTokenProvider;
import com.koreanguide.koreanguidebackend.domain.appointment.data.dto.response.AppointmentMainResponseDto;
import com.koreanguide.koreanguidebackend.domain.appointment.service.AppointmentService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1/appointment")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;
    private final JwtTokenProvider jwtTokenProvider;

    public Long GET_USER_ID_BY_TOKEN(HttpServletRequest request) {
        return jwtTokenProvider.getUserIdByToken(request.getHeader("X-AUTH-TOKEN"));
    }

    @ApiOperation(value = "사용자 모든 약속(일정) 조회")
    @GetMapping("/")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public ResponseEntity<List<AppointmentMainResponseDto>> getAppointmentInfoUsedByMain(HttpServletRequest request) {
        return appointmentService.getAppointmentInfoUsedByMain(GET_USER_ID_BY_TOKEN(request));
    }

    @PostMapping("/cancel")
    public ResponseEntity<?> requestCancelAppointment(HttpServletRequest request,
                                                      @RequestParam Long appointmentId) {
        return appointmentService.requestCancelAppointment(GET_USER_ID_BY_TOKEN(request), appointmentId);
    }
}
