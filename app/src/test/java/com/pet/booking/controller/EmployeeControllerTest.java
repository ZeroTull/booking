package com.pet.booking.controller;

import com.pet.booking.dto.EmployeeDTO;
import com.pet.booking.enums.ServiceTypeName;
import com.pet.booking.models.Employee;
import com.pet.booking.models.Service;
import com.pet.booking.repo.EmployeeRepo;
import io.unified.verify.hard.Verify;
import io.unified.verify.soft.Verifier;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmployeeControllerTest {

    @Mock
    private EmployeeRepo employeeRepo;

    @InjectMocks
    private EmployeeController employeeController;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void findByIdDoesNotLeakThePasswordField() {
        long employeeId = 1L;
        Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setFirstName("Jane");
        employee.setLastName("Doe");
        employee.setEmail("jane.doe@mail.mail");
        employee.setPhoneNumber("555-1234");
        employee.setPassword("super-secret");

        when(employeeRepo.findById(employeeId)).thenReturn(Optional.of(employee));

        EmployeeDTO actual = employeeController.findById(employeeId);

        // findById used to return the raw Employee entity, serializing its password field
        // straight into the JSON response. EmployeeDTO has no password field at all, so it's
        // structurally impossible for the fields below to include it.
        Verifier verifier = new Verifier();
        verifier.Long.equals(actual.getId(), employeeId);
        verifier.String.equals(actual.getFirstName(), "Jane");
        verifier.String.equals(actual.getLastName(), "Doe");
        verifier.String.equals(actual.getEmail(), "jane.doe@mail.mail");
        verifier.String.equals(actual.getPhoneNumber(), "555-1234");
        verifier.verify();
    }

    @Test
    public void findNotExistingUserById() {
        long invalidId = 100L;
        when(employeeRepo.findById(invalidId)).thenReturn(Optional.empty());

        ResponseStatusException exception = Assert.expectThrows(ResponseStatusException.class,
                () -> employeeController.findById(invalidId));

        Verify.Object.equals(exception.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void updateEmployeeAppliesAllFieldsButNeverPasswordOrAdmin() {
        long employeeId = 1L;
        Service originalService = new Service();
        originalService.setServiceTypeName(ServiceTypeName.TYPE_1);

        Employee existing = new Employee();
        existing.setId(employeeId);
        existing.setFirstName("Old");
        existing.setLastName("OldLast");
        existing.setPhoneNumber("555-0000");
        existing.setEmail("old@mail.mail");
        existing.setServices(List.of(originalService));
        existing.setPassword("existing-hash");
        existing.setAdmin(true);

        Service newService = new Service();
        newService.setServiceTypeName(ServiceTypeName.TYPE_2);

        // A request that changes every regular field, and also (whether maliciously or by a
        // careless client sending the whole entity back) tries to smuggle in a different
        // password and a demoted admin flag -- neither should reach the saved entity.
        Employee update = new Employee();
        update.setFirstName("New");
        update.setLastName("NewLast");
        update.setPhoneNumber("555-1111");
        update.setEmail("new@mail.mail");
        update.setServices(List.of(newService));
        update.setPassword("attacker-supplied");
        update.setAdmin(false);

        when(employeeRepo.findById(employeeId)).thenReturn(Optional.of(existing));

        employeeController.updateEmployee(employeeId, update);

        ArgumentCaptor<Employee> savedCaptor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepo).save(savedCaptor.capture());
        Employee saved = savedCaptor.getValue();

        Verifier verifier = new Verifier();
        // Regular fields: all take the new request values.
        verifier.String.equals(saved.getFirstName(), "New");
        verifier.String.equals(saved.getLastName(), "NewLast");
        verifier.String.equals(saved.getPhoneNumber(), "555-1111");
        verifier.String.equals(saved.getEmail(), "new@mail.mail");
        verifier.Object.equals(saved.getServices(), List.of(newService));
        // Sensitive fields: keep their original values no matter what the request contains.
        verifier.String.equals(saved.getPassword(), "existing-hash");
        verifier.Bool.isTrue(saved.isAdmin());
        verifier.verify();
    }
}
