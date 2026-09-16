package com.pet.booking.controller.calendar;

import com.pet.booking.models.bookingCalendar.Appointment;
import com.pet.booking.repo.AppointmentRepo;
import jakarta.websocket.server.PathParam;
import lombok.extern.slf4j.Slf4j;
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

    @GetMapping(path = "/getAllAppointment")
    public Iterable<Appointment> getAllAppointments() {
        return appointmentRepo.findAll();
    }

    @GetMapping(path = "/getEmployeeAppointments/{employeeId}")
    public Iterable<Appointment> getAppointmentsByEmployee(@PathVariable int employeeId) {
        return ResponseEntity.ok(appointmentRepo.findAllByEmployee_Id(employeeId)).getBody();
    }

    @GetMapping(path = "/getEmployeeAppointments")
    public Iterable<Appointment> getAppointmentsByCustomerEmail(@PathParam(value = "email") String email) {
        return ResponseEntity.ok(appointmentRepo.findAllByCustomer_Email(email)).getBody();
    }

    @PostMapping(path = "/addAppointment", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addAppointment(@RequestBody final Appointment appointment) {
        if (appointment.getEmployee() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Employee is required.");
        }
        if (appointment.getCustomer() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Customer is required.");
        }

        //check that new appointment is not in the range of already existing appointments of employee.
        List<Appointment> appointmentList = appointmentRepo.findAppointmentsByEmployee_Id(appointment.getEmployee().getId());

        LocalDateTime newStart = appointment.getDateTime();
        LocalDateTime newEnd = newStart.plusMinutes(appointment.getService().getDurationInMinutes());
        for (Appointment a : appointmentList) {
            LocalDateTime existingStart = a.getDateTime();
            LocalDateTime existingEnd = existingStart.plusMinutes(a.getService().getDurationInMinutes());
            boolean overlaps = newStart.isBefore(existingEnd) && existingStart.isBefore(newEnd);
            if (overlaps) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("This time slot is not available.");
            }
        }

        //TODO check if this validation is needed now
        Appointment appointmentByEmployeeIdAndDate = appointmentRepo.findAppointmentByEmployee_IdAndDateTime(appointment.getEmployee().getId(), appointment.getDateTime());
        if (appointment.getDateTime().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Cannot create appointment in the past.");
        } else if (appointmentByEmployeeIdAndDate != null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Appointment for this date already exists.");
        }

        appointmentRepo.save(appointment);
        log.info("Created appointment for " + appointment.getDateTime());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Created appointment for " + appointment.getDateTime());
    }


    @DeleteMapping(path = "/{appointmentId}")
    public ResponseEntity<String> deleteAppointment(long appointmentId) {
        if (appointmentRepo.findById(appointmentId) == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Appointment with this id does not exist.");
        }

        appointmentRepo.deleteById(appointmentId);
        log.info(String.format("Appointment #%s deleted.", appointmentId));
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}


