package com.pet.booking.controller;

import com.pet.booking.dto.CustomerDTO;
import com.pet.booking.models.Customer;
import com.pet.booking.repo.CustomerRepo;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static com.pet.booking.controller.ApiDefinition.CUSTOMER_RESOURCE_ROOT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping(value = CUSTOMER_RESOURCE_ROOT)
public class CustomerController {
    @Autowired
    private CustomerRepo customerRepo;
    private final ModelMapper mapper = new ModelMapper();
    Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @PostMapping(value = "/add", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpStatus> addEmployee(@RequestBody final Customer customer) {
        //add email constraint validation
        if (customerRepo.findByEmail(customer.getEmail()) != null) {
            //return 400 with constraint message //
            // todo message is not displayed in response
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Employee with %s email already exists.", customer.getEmail()));
        }
        customerRepo.save(customer);
        logger.info("Created customer with " + customer.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping(value = "/delete", consumes = APPLICATION_JSON_VALUE)
    public void addEmployee(@RequestHeader long id) {
        //add verification if employee exists
        customerRepo.deleteById(id);
        logger.info(String.format("Deleted customer with %s id.", id));
    }

    @PutMapping(value = "/update/{id}", consumes = APPLICATION_JSON_VALUE)
    public void updateEmployee(@PathVariable long id, @RequestBody Customer employee) {
        Optional<Customer> findById = customerRepo.findById(id);

        if (findById.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("User with %s id does not exist.", id));
        }
        Customer customerToUpdate = findById.get();
        customerToUpdate.setFirstName(employee.getFirstName());
        customerToUpdate.setLastName(employee.getLastName());
        customerToUpdate.setPhoneNumber(employee.getPhoneNumber());
        customerToUpdate.setEmail(employee.getEmail());
        customerRepo.save(customerToUpdate);
    }

    @GetMapping(value = "/{id}")
    public CustomerDTO findById(@PathVariable long id) {
        Optional<Customer> customer = customerRepo.findById(id);
        if (customer.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("User with %s id does not exist.", id));
        }
        return ResponseEntity.ok(mapper.map(customer, CustomerDTO.class)).getBody();
    }
}
