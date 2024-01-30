package com.pet.booking.controller.calendar;

import com.pet.booking.models.bookingCalendar.Appointment;
import com.pet.booking.repo.AppointmentRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import static com.pet.booking.controller.base.ApiDefinition.APPOINTMENT_RESOURCE_ROOT;

@Slf4j
@RestController
@RequestMapping(value = APPOINTMENT_RESOURCE_ROOT)
public class AppointmentController {

    @Autowired
    AppointmentRepo appointmentRepo;

    @GetMapping(path = "/getAllAppointment")
    public Iterable<Appointment> getAllBookings() {
        return appointmentRepo.findAll();
    }

    @GetMapping(path = "/getEmployeeAppointments/{id}")
    public Iterable<Appointment> getAllBookingsForEmployee(@PathVariable int id) {
        return appointmentRepo.findAllByEmployeeId(id);
    }

    @PostMapping(path = "/addAppointment", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addBookingEntry(@RequestBody final Appointment appointment) {
        appointmentRepo.save(appointment);
    }
}


