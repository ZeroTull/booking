package com.pet.booking.repo;

import com.pet.booking.models.bookingCalendar.Appointment;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AppointmentRepo extends CrudRepository<Appointment, Long> {
    List<Appointment> findAllByEmployeeId(long employeeId);
}
