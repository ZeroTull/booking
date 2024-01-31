package com.pet.booking.controller;

import com.pet.booking.dto.CustomerDTO;
import com.pet.booking.models.Customer;
import com.pet.booking.repo.CustomerRepo;
import org.apache.commons.lang3.RandomStringUtils;
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
import static org.testng.Assert.assertEquals;

public class CustomerControllerTest {

    @Mock
    private CustomerRepo customerRepo;

    @InjectMocks
    private CustomerController customerController;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindById_WithValidId_ReturnsCustomerDTO() {
        // Arrange
        long customerId = 1L;
        String email = RandomStringUtils.randomAlphabetic(5).concat("@mail.mail");
        Customer customer = new Customer();

        customer.setId(customerId);
        customer.setEmail(email);
        customerRepo.save(customer);

        // Assuming CustomerDTO is a simple mapping of Customer
        CustomerDTO expectedDto = new CustomerDTO();
        expectedDto.setId(customerId);
        expectedDto.setEmail(email);

        when(customerRepo.findById(customerId)).thenReturn(Optional.of(customer));

        // Act
        CustomerDTO actualDto = customerController.findById(customerId);

        // Assert
        assertEquals(actualDto.getEmail(), expectedDto.getEmail());
    }

    @Test
    public void testFindById_WithInvalidId_ThrowsResponseStatusException() {
        // Arrange
        long invalidId = 100L;
        when(customerRepo.findById(invalidId)).thenReturn(Optional.empty());

        // Act & Assert
        try {
            customerController.findById(invalidId);
            // If no exception is thrown, fail the test
            Assert.fail("Expected ResponseStatusException was not thrown");
        } catch (ResponseStatusException exception) {
            // Assert the properties of the thrown exception
            assertEquals(exception.getStatusCode(), HttpStatus.BAD_REQUEST);
            assertEquals(exception.getReason(), String.format("User with %s id does not exist.", invalidId));
        }
    }
}
