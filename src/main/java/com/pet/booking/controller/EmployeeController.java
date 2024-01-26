package com.pet.booking.controller;

import com.pet.booking.models.Employee;
import com.pet.booking.repo.EmployeeRepo;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static com.pet.booking.controller.ApiDefinition.EMPLOYEE_RESOURCE_ROOT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping(value = EMPLOYEE_RESOURCE_ROOT)
public class EmployeeController {
    @Autowired
    private EmployeeRepo employeeRepo;
    Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @GetMapping(value = "/getEmployees")
    public Iterable<Employee> getEmployees() {
        return employeeRepo.findAllByOrderByIdDesc();
    }

    @PostMapping(value = "/add", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpStatus> addEmployee(@RequestBody final Employee employee) {
        //add email constraint validation
        if (employeeRepo.findByEmail(employee.getEmail()) != null) {
            //return 400 with constraint message //
            // todo message is not displayed in response
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Employee with %s email already exists.", employee.getEmail()));
        }
        employeeRepo.save(employee);
        logger.info("Created user with " + employee.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping(value = "/delete")
    public void addEmployee(@RequestHeader long id) {
        //add verification if employee exists
        employeeRepo.deleteById(id);
        logger.info(String.format("Deleted user with %s id.", id));
    }

    @PutMapping(value = "/update/{id}")
    public void updateEmployee(@PathVariable long id, @RequestBody Employee employee) {
        Optional<Employee> findById = employeeRepo.findById(id);

        if (findById.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("User with %s id does not exist.", id));
        }
        Employee updatedEmployee = findById.get();

        updatedEmployee.setFirstName(employee.getFirstName());
        updatedEmployee.setLastName(employee.getLastName());
        updatedEmployee.setJobTypes(employee.getJobTypes());
        updatedEmployee.setEmail(employee.getEmail());
        updatedEmployee.setPassword(employee.getPassword());  // Todo add separate service to create/update password
        updatedEmployee.setPhoneNumber(employee.getPhoneNumber());
        updatedEmployee.setAdmin(employee.isAdmin());

        employeeRepo.save(updatedEmployee);
    }

    @GetMapping(value = "/{id}")
    public Employee findById(@PathVariable long id) {
        Optional<Employee> employee = employeeRepo.findById(id);
        if (employee.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("User with %s id does not exist.", id));
        }
        return ResponseEntity.ok(employee.get()).getBody();
    }
}
