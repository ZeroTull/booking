package com.pet.booking.controller.calendar;

import com.pet.booking.dto.AppointmentDTO;
import com.pet.booking.enums.ServiceTypeName;
import com.pet.booking.models.Customer;
import com.pet.booking.models.Employee;
import com.pet.booking.models.Service;
import com.pet.booking.models.calendar.Appointment;
import com.pet.booking.repo.AppointmentRepo;
import io.unified.verify.hard.Verify;
import io.unified.verify.soft.Verifier;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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

    private static Employee employee(long id) {
        Employee employee = new Employee();
        employee.setId(id);
        return employee;
    }

    private static Customer customer(long id) {
        Customer customer = new Customer();
        customer.setId(id);
        return customer;
    }

    private static Appointment appointment(long employeeId, LocalDateTime dateTime, Service service) {
        Appointment appointment = new Appointment();
        appointment.setEmployee(employee(employeeId));
        appointment.setCustomer(customer(1L));
        appointment.setDateTime(dateTime);
        appointment.setService(service);
        return appointment;
    }

    @Test
    public void rejectsAppointmentThatOverlapsExisting() {
        long employeeId = 1L;
        LocalDateTime existingStart = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        Appointment existing = appointment(employeeId, existingStart, service(60));

        // starts 30 minutes into the existing 60-minute appointment
        Appointment overlapping = appointment(employeeId, existingStart.plusMinutes(30), service(30));

        when(appointmentRepo.findAppointmentsByEmployee_Id(employeeId)).thenReturn(List.of(existing));

        ResponseEntity<String> response = appointmentController.addAppointment(overlapping);

        Verify.Object.equals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Verify.String.equals(response.getBody(), "This time slot is not available.");
    }

    @Test
    public void acceptsAppointmentImmediatelyAfterExistingEnds() {
        long employeeId = 1L;
        LocalDateTime existingStart = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        Appointment existing = appointment(employeeId, existingStart, service(60));

        // starts exactly when the existing 60-minute appointment ends -- no overlap
        Appointment adjacent = appointment(employeeId, existingStart.plusMinutes(60), service(30));

        when(appointmentRepo.findAppointmentsByEmployee_Id(employeeId)).thenReturn(List.of(existing));
        when(appointmentRepo.findAppointmentByEmployee_IdAndDateTime(employeeId, adjacent.getDateTime())).thenReturn(null);

        ResponseEntity<String> response = appointmentController.addAppointment(adjacent);

        Verify.Object.equals(response.getStatusCode(), HttpStatus.CREATED);
        Verify.String.contains(response.getBody(), "Created appointment for");
    }

    @Test
    public void acceptsAppointmentBeforeExistingStarts() {
        long employeeId = 1L;
        LocalDateTime existingStart = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        Appointment existing = appointment(employeeId, existingStart, service(60));

        // ends exactly when the existing appointment starts -- no overlap
        Appointment before = appointment(employeeId, existingStart.minusMinutes(30), service(30));

        when(appointmentRepo.findAppointmentsByEmployee_Id(employeeId)).thenReturn(List.of(existing));
        when(appointmentRepo.findAppointmentByEmployee_IdAndDateTime(employeeId, before.getDateTime())).thenReturn(null);

        ResponseEntity<String> response = appointmentController.addAppointment(before);

        Verify.Object.equals(response.getStatusCode(), HttpStatus.CREATED);
        Verify.String.contains(response.getBody(), "Created appointment for");
    }

    @Test
    public void rejectsAppointmentWithNoEmployee() {
        Appointment appointment = appointment(1L, LocalDateTime.now().plusDays(1), service(30));
        appointment.setEmployee(null);

        ResponseEntity<String> response = appointmentController.addAppointment(appointment);

        Verify.Object.equals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Verify.String.equals(response.getBody(), "Employee is required.");
    }

    @Test
    public void rejectsAppointmentWithNoCustomer() {
        Appointment appointment = appointment(1L, LocalDateTime.now().plusDays(1), service(30));
        appointment.setCustomer(null);

        ResponseEntity<String> response = appointmentController.addAppointment(appointment);

        Verify.Object.equals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Verify.String.equals(response.getBody(), "Customer is required.");
    }

    @Test
    public void deletesAppointmentThatExists() {
        long appointmentId = 5L;
        when(appointmentRepo.findById(appointmentId)).thenReturn(appointment(1L, LocalDateTime.now().plusDays(1), service(30)));

        ResponseEntity<String> response = appointmentController.deleteAppointment(appointmentId);

        Verify.Object.equals(response.getStatusCode(), HttpStatus.OK);
        verify(appointmentRepo).deleteById(appointmentId);
    }

    @Test
    public void rejectsDeleteOfAppointmentThatDoesNotExist() {
        long appointmentId = 5L;
        when(appointmentRepo.findById(appointmentId)).thenReturn(null);

        ResponseEntity<String> response = appointmentController.deleteAppointment(appointmentId);

        Verify.Object.equals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        Verify.String.equals(response.getBody(), "Appointment with this id does not exist.");
        verify(appointmentRepo, never()).deleteById(appointmentId);
    }

    @Test
    public void getAllAppointmentsMapsToDtosWithoutLeakingEmployeePassword() {
        Employee employeeEntity = employee(1L);
        employeeEntity.setFirstName("Jane");
        employeeEntity.setPassword("super-secret");

        Customer customerEntity = customer(2L);
        customerEntity.setFirstName("John");

        Appointment appointment = new Appointment();
        appointment.setId(9L);
        appointment.setEmployee(employeeEntity);
        appointment.setCustomer(customerEntity);
        appointment.setDateTime(LocalDateTime.now().plusDays(1));
        appointment.setService(service(30));

        when(appointmentRepo.findAll()).thenReturn(List.of(appointment));

        List<AppointmentDTO> result = appointmentController.getAllAppointments();

        Verifier verifier = new Verifier();
        verifier.Int.equals(result.size(), 1);
        AppointmentDTO dto = result.get(0);
        verifier.Long.equals(dto.getId(), 9L);
        verifier.String.equals(dto.getEmployee().getFirstName(), "Jane");
        verifier.String.equals(dto.getCustomer().getFirstName(), "John");
        verifier.Bool.isTrue(dto.isActive());
        // EmployeeDTO has no password field at all -- structurally impossible to leak it here,
        // same guarantee as EmployeeController.findById's fix.
        verifier.verify();
    }
}
