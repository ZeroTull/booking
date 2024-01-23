package com.pet.booking.controller;

import com.pet.booking.models.Employee;
import com.pet.booking.repo.EmployeeRepo;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.pet.booking.controller.ApiDefinition.EMPLOYEE_RESOURCE_ROOT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping(value = EMPLOYEE_RESOURCE_ROOT)
public class EmployeeController {
    @Autowired
    private EmployeeRepo employeeRepo;
    Logger logger = LoggerFactory.getLogger(EmployeeController.class);


    @PostMapping(value = "/addEmployee", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpStatus> addEmployee(@RequestBody final Employee employee) {
        //add email constraint validation
        if (employeeRepo.findByEmail(employee.getEmail()) != null) {
            //return 400 with constraint message
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Employee with %s email already exists.", employee.getEmail()));
        }

        employeeRepo.save(employee);
        logger.info("Created user with " + employee.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping(value = "/delete")
    public void addEmployee(@RequestHeader long id) {
        //check if employee exists
        employeeRepo.deleteById(id);
        logger.info(String.format("Deleted user with %s id.", id));
    }

    @GetMapping(value = "/getEmployees")
    public List<Employee> getEmployees() {
        return employeeRepo.findAll();
    }
}
