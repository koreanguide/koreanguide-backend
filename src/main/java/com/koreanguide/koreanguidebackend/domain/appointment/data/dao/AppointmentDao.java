package com.koreanguide.koreanguidebackend.domain.appointment.data.dao;

import com.koreanguide.koreanguidebackend.domain.appointment.data.entity.Appointment;
import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;

import java.util.List;

public interface AppointmentDao {
    void saveAppointmentEntity(Appointment appointment);

    List<Appointment> getAppointmentListByUser(User user);

    Appointment getAppointmentEntity(Long appointmentId);
}
