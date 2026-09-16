package com.pet.booking.repo;

import com.pet.booking.models.bookingCalendar.Appointment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepo extends CrudRepository<Appointment, Long> {
    List<Appointment> findAllByEmployee_Id(long employeeId);

    List<Appointment> findAllByCustomer_Email(String customerEmail);

    Appointment findAppointmentByEmployee_IdAndDateTime(long employeeId, LocalDateTime date);

    List<Appointment> findAppointmentsByEmployee_Id(long employeeId);

    void deleteById(long appointmentId);

    Appointment findById(long appointmentId);
}
