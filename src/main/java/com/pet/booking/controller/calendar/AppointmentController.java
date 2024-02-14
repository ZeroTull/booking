package com.pet.booking.controller.calendar;

import com.pet.booking.controller.CustomerController;
import com.pet.booking.models.bookingCalendar.Appointment;
import com.pet.booking.repo.AppointmentRepo;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.pet.booking.controller.base.ApiDefinition.APPOINTMENT_RESOURCE_ROOT;

@Slf4j
@RestController
@RequestMapping(value = APPOINTMENT_RESOURCE_ROOT)
public class AppointmentController {

    @Autowired
    AppointmentRepo appointmentRepo;
    Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @GetMapping(path = "/getAllAppointment")
    public Iterable<Appointment> getAllAppointments() {
        return appointmentRepo.findAll();
    }

    @GetMapping(path = "/getEmployeeAppointments/{employeeId}")
    public Iterable<Appointment> getAppointmentsByEmployee(@PathVariable int employeeId) {
        return ResponseEntity.ok(appointmentRepo.findAllByEmployeeId(employeeId)).getBody();
    }

    @PostMapping(path = "/addAppointment", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addAppointment(@RequestBody final Appointment appointment) {

        if (appointmentRepo.findAllByEmployeeIdAndDate(appointment.getEmployeeId(), appointment.getDate()) != null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Appointment for this date already exists.");
        }
        appointmentRepo.save(appointment);
        logger.info("Created appointment for " + appointment.getDate());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping(path = "/{appointmentId}")
    public ResponseEntity deleteAppointment(long appointmentId) {
        if (appointmentRepo.findById(appointmentId) != null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Appointment with this id does not exist.");
        }

        appointmentRepo.deleteById(appointmentId);
        logger.info("Appointment with %s deleted." + appointmentId);
        return ResponseEntity.status(HttpStatus.OK).build();

    }
}


