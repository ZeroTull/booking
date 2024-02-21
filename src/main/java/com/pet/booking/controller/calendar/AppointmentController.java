package com.pet.booking.controller.calendar;

import com.pet.booking.controller.CustomerController;
import com.pet.booking.models.bookingCalendar.Appointment;
import com.pet.booking.repo.AppointmentRepo;
import jakarta.websocket.server.PathParam;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

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

    @GetMapping(path = "/getEmployeeAppointments")
    public Iterable<Appointment> getAppointmentsByCustomerEmail(@PathParam(value = "email") String email) {
        return ResponseEntity.ok(appointmentRepo.findAllByCustomerEmail(email)).getBody();
    }

    @PostMapping(path = "/addAppointment", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addAppointment(@RequestBody final Appointment appointment) {
        //check that new appointment is not in the range of already existing appointments of employee.
        List<Appointment> appointmentList = appointmentRepo.findAppointmentsByEmployeeId(appointment.getEmployeeId());

        //TODO - test this manually + create some tests for this
        for (Appointment a : appointmentList) {
            if (appointment.getDateTime().isAfter(a.getDateTime()) || appointment.getDateTime().isBefore(a.getDateTime().plusMinutes(a.getService().getDurationInMinutes()))) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("This time slot is not available.");
            }
        }


        //TODO check if this validtion is needed now
        Appointment appointmentByEmployeeIdAndDate = appointmentRepo.findAppointmentByEmployeeIdAndDateTime(appointment.getEmployeeId(), appointment.getDateTime());
        if (appointment.getDateTime().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Cannot create appointment in the past.");
        } else if (appointmentByEmployeeIdAndDate != null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Appointment for this date already exists.");
        }
        appointmentRepo.save(appointment);
        logger.info("Created appointment for " + appointment.getDateTime());
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


