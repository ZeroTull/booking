package com.pet.booking.repo;

import com.pet.booking.models.bookingCalendar.Appointment;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepo extends CrudRepository<Appointment, Long> {
    List<Appointment> findAllByEmployeeId(long employeeId);
    List<Appointment> findAllByCustomerEmail(String customerEmail);

    Appointment findAppointmentByEmployeeIdAndDateTime(long employeeId, LocalDateTime date);

    List<Appointment> findAppointmentsByEmployeeId(long employeeId);
    void deleteById(long appointmentId);
    Appointment findById(long appointmentId);
}
