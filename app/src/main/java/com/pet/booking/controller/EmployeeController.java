package com.pet.booking.controller;

import com.pet.booking.dto.EmployeeDTO;
import com.pet.booking.models.Employee;
import com.pet.booking.repo.EmployeeRepo;
import com.pet.booking.utils.ObjectMapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static com.pet.booking.controller.base.ApiDefinition.EMPLOYEE_RESOURCE_ROOT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping(value = EMPLOYEE_RESOURCE_ROOT)
public class EmployeeController {
    @Autowired
    private EmployeeRepo employeeRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @GetMapping(value = "/getEmployees")
    public List<EmployeeDTO> getEmployees() {
        return ObjectMapperUtils.mapAll(employeeRepo.findAllByOrderByIdDesc(), EmployeeDTO.class);
    }

    @PostMapping(value = "/add", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity addEmployee(@RequestBody final Employee employee) {
        //Check if employee with provided email exist.
        if (employeeRepo.findByEmail(employee.getEmail()) != null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(String.format("Employee with %s email already exists.", employee.getEmail()));
        }
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        employeeRepo.save(employee);
        logger.info("Created employee with " + employee.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping(value = "/delete")
    public void deleteEmployee(@RequestHeader long id) {
        //add verification if employee exists
        employeeRepo.deleteById(id);
        logger.info(String.format("Deleted user with %s id.", id));
    }

    @PutMapping(value = "/update/{id}")
    public void updateEmployee(@PathVariable long id, @RequestBody Employee employee) {
        Employee employeeToUpdate = employeeRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("User with %s id does not exist.", id)));

        // Deliberately not copying password/isAdmin here: this endpoint has no auth
        // enforcement, so those stay out of the general-purpose field update on purpose,
        // not just because of the pre-existing "separate service for password" TODO.
        employeeToUpdate.setFirstName(employee.getFirstName());
        employeeToUpdate.setLastName(employee.getLastName());
        employeeToUpdate.setPhoneNumber(employee.getPhoneNumber());
        employeeToUpdate.setEmail(employee.getEmail());
        employeeToUpdate.setServices(employee.getServices());

        employeeRepo.save(employeeToUpdate);
        logger.info(String.format("Updated employee with %s id.", id));
    }

    @GetMapping(value = "/{id}")
    public EmployeeDTO findById(@PathVariable long id) {
        Optional<Employee> employee = employeeRepo.findById(id);
        if (employee.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Employee with %s id does not exist.", id));
        }
        return ObjectMapperUtils.map(employee.get(), EmployeeDTO.class);
    }
}
