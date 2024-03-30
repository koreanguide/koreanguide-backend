package com.koreanguide.koreanguidebackend.domain.appointment.data.repository;

import com.koreanguide.koreanguidebackend.domain.appointment.data.dto.entity.Appointment;
import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> getAppointmentByGuide(User user);
}
