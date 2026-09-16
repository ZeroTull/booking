package com.pet.booking.controller.calendar;

import com.pet.booking.enums.ServiceTypeName;
import com.pet.booking.models.Service;
import com.pet.booking.models.bookingCalendar.Appointment;
import com.pet.booking.repo.AppointmentRepo;
import io.unified.verify.hard.Verify;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;

public class AppointmentControllerTest {

    @Mock
    private AppointmentRepo appointmentRepo;

    @InjectMocks
    private AppointmentController appointmentController;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private static Service service(int durationInMinutes) {
        Service service = new Service();
        service.setServiceTypeName(ServiceTypeName.TYPE_1);
        service.setDurationInMinutes(durationInMinutes);
        service.setPrice(100);
        return service;
    }

    private static Appointment appointment(int employeeId, LocalDateTime dateTime, Service service) {
        Appointment appointment = new Appointment();
        appointment.setEmployeeId(employeeId);
        appointment.setDateTime(dateTime);
        appointment.setService(service);
        return appointment;
    }

    @Test
    public void rejectsAppointmentThatOverlapsExisting() {
        int employeeId = 1;
        LocalDateTime existingStart = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        Appointment existing = appointment(employeeId, existingStart, service(60));

        // starts 30 minutes into the existing 60-minute appointment
        Appointment overlapping = appointment(employeeId, existingStart.plusMinutes(30), service(30));

        when(appointmentRepo.findAppointmentsByEmployeeId(employeeId)).thenReturn(List.of(existing));

        ResponseEntity<String> response = appointmentController.addAppointment(overlapping);

        Verify.Object.equals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Verify.String.equals(response.getBody(), "This time slot is not available.");
    }

    @Test
    public void acceptsAppointmentImmediatelyAfterExistingEnds() {
        int employeeId = 1;
        LocalDateTime existingStart = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        Appointment existing = appointment(employeeId, existingStart, service(60));

        // starts exactly when the existing 60-minute appointment ends -- no overlap
        Appointment adjacent = appointment(employeeId, existingStart.plusMinutes(60), service(30));

        when(appointmentRepo.findAppointmentsByEmployeeId(employeeId)).thenReturn(List.of(existing));
        when(appointmentRepo.findAppointmentByEmployeeIdAndDateTime(employeeId, adjacent.getDateTime())).thenReturn(null);

        ResponseEntity<String> response = appointmentController.addAppointment(adjacent);

        Verify.Object.equals(response.getStatusCode(), HttpStatus.CREATED);
        Verify.String.contains(response.getBody(), "Created appointment for");
    }

    @Test
    public void acceptsAppointmentBeforeExistingStarts() {
        int employeeId = 1;
        LocalDateTime existingStart = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        Appointment existing = appointment(employeeId, existingStart, service(60));

        // ends exactly when the existing appointment starts -- no overlap
        Appointment before = appointment(employeeId, existingStart.minusMinutes(30), service(30));

        when(appointmentRepo.findAppointmentsByEmployeeId(employeeId)).thenReturn(List.of(existing));
        when(appointmentRepo.findAppointmentByEmployeeIdAndDateTime(employeeId, before.getDateTime())).thenReturn(null);

        ResponseEntity<String> response = appointmentController.addAppointment(before);

        Verify.Object.equals(response.getStatusCode(), HttpStatus.CREATED);
        Verify.String.contains(response.getBody(), "Created appointment for");
    }
}
