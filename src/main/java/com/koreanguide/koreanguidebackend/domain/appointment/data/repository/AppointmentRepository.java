package com.koreanguide.koreanguidebackend.domain.appointment.data.repository;

import com.koreanguide.koreanguidebackend.domain.appointment.data.entity.Appointment;
import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.Track;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> getAppointmentByGuide(User user);
    List<Appointment> getAppointmentByTrack(Track track);
}
