package com.pet.booking.controller;

import com.pet.booking.dto.EmployeeDTO;
import com.pet.booking.models.Employee;
import com.pet.booking.repo.EmployeeRepo;
import io.unified.verify.hard.Verify;
import io.unified.verify.soft.Verifier;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Optional;

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
}
