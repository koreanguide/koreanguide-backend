package com.koreanguide.koreanguidebackend.domain.appointment.data.dao.Impl;

import com.koreanguide.koreanguidebackend.domain.appointment.data.dao.AppointmentDao;
import com.koreanguide.koreanguidebackend.domain.appointment.data.entity.Appointment;
import com.koreanguide.koreanguidebackend.domain.appointment.data.repository.AppointmentRepository;
import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AppointmentDaoImpl implements AppointmentDao {
    private final AppointmentRepository appointmentRepository;

    @Override
    public void saveAppointmentEntity(Appointment appointment) {
        appointmentRepository.save(appointment);
    }

    @Override
    public List<Appointment> getAppointmentListByUser(User user) {
        return appointmentRepository.getAppointmentByGuide(user);
    }

    @Override
    public Appointment getAppointmentEntity(Long appointmentId) {
        return appointmentRepository.getById(appointmentId);
    }
}
