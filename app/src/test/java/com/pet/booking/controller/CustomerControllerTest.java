package com.pet.booking.controller;

import com.pet.booking.dto.CustomerDTO;
import com.pet.booking.models.Customer;
import com.pet.booking.repo.CustomerRepo;
import io.unified.verify.hard.Verify;
import io.unified.verify.soft.Verifier;
import org.apache.commons.lang3.RandomStringUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Optional;

import static org.mockito.Mockito.when;

public class CustomerControllerTest {

    @Mock
    private CustomerRepo customerRepo;

    @InjectMocks
    private CustomerController customerController;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

//    @Test
//    public void createNewCustomer() {
//        String customerEmail = RandomStringUtils.randomAlphabetic(5).concat("@gmail.com");
//        Customer expected = new Customer();
//        expected.setFirstName(RandomStringUtils.randomAlphabetic(5));
//        expected.setLastName(RandomStringUtils.randomAlphabetic(5));
//        expected.setEmail(customerEmail);
//        expected.setPassword(RandomStringUtils.randomAlphanumeric(12));
//        expected.setPhoneNumber(RandomStringUtils.randomNumeric(9));
//
//
//        customerController.addCustomer(expected);
//        Customer actual = customerRepo.findByEmail(customerEmail);
//        expected.setId(actual.getId());
//
//        assertEquals(actual, expected);
//    }
//
//    @Test
//    public void updateCustomer() {
//        //create customer
//        String customerEmail = RandomStringUtils.randomAlphabetic(5).concat("@gmail.com");
//        Customer expected = new Customer();
//        expected.setFirstName(RandomStringUtils.randomAlphabetic(5));
//        expected.setLastName(RandomStringUtils.randomAlphabetic(5));
//        expected.setEmail(customerEmail);
//        expected.setPassword(RandomStringUtils.randomAlphanumeric(12));
//        expected.setPhoneNumber(RandomStringUtils.randomNumeric(9));
//
//        //validate customer creation
//        customerController.addCustomer(expected);
//        Customer actual = customerRepo.findByEmail(customerEmail);
//        expected.setId(actual.getId());
//        assertEquals(actual, expected);
//
//        //update customer with new values
//        String updatedEmail = RandomStringUtils.randomAlphabetic(5).concat("@gmail.com");
//        expected.setFirstName(RandomStringUtils.randomAlphabetic(5));
//        expected.setLastName(RandomStringUtils.randomAlphabetic(5));
//        expected.setEmail(updatedEmail);
//        expected.setPassword(RandomStringUtils.randomAlphanumeric(12));
//        expected.setPhoneNumber(RandomStringUtils.randomNumeric(9));
//
//        //get updated customer
//        actual = customerRepo.findByEmail(customerEmail);
//
//        //validate updated customer
//        assertEquals(actual, expected);
//    }
//
//    @Test
//    public void deleteCustomer() {
//        //create customer
//        String customerEmail = RandomStringUtils.randomAlphabetic(5).concat("@gmail.com");
//        Customer expected = new Customer();
//        expected.setFirstName(RandomStringUtils.randomAlphabetic(5));
//        expected.setLastName(RandomStringUtils.randomAlphabetic(5));
//        expected.setEmail(customerEmail);
//        expected.setPassword(RandomStringUtils.randomAlphanumeric(12));
//        expected.setPhoneNumber(RandomStringUtils.randomNumeric(9));
//        expected = customerRepo.findByEmail(customerEmail);
//
//        //delete customer
//        customerController.deleteCustomer(expected.getId());
//        //find in db
//        expected = customerRepo.findByEmail(customerEmail);
//        //validate user deleted
//        assertNull(expected);
//    }

    @Test
    public void findExistingUserById() {
        // Arrange
        long customerId = 1L;
        String email = RandomStringUtils.randomAlphabetic(5).concat("@mail.mail");
        Customer customer = new Customer();

        customer.setId(customerId);
        customer.setEmail(email);
        customerRepo.save(customer);

        // Assuming CustomerDTO is a simple mapping of Customer
        CustomerDTO expectedDto = new CustomerDTO();
        expectedDto.setEmail(email);

        when(customerRepo.findById(customerId)).thenReturn(Optional.of(customer));

        // Act
        CustomerDTO actualDto = customerController.findById(customerId);

        // Assert
        Verify.String.equals(actualDto.getEmail(), expectedDto.getEmail());
    }

    @Test
    public void findNotExistingUserById() {
        // Arrange
        long invalidId = 100L;
        when(customerRepo.findById(invalidId)).thenReturn(Optional.empty());

        // Act & Assert
        try {
            customerController.findById(invalidId);
            // If no exception is thrown, fail the test
            Assert.fail("Expected ResponseStatusException was not thrown");
        } catch (ResponseStatusException exception) {
            // Assert both properties of the thrown exception together, so a mismatch on
            // either one is reported in a single run instead of masking the other.
            Verifier verifier = new Verifier();
            verifier.Object.equals(exception.getStatusCode(), HttpStatus.BAD_REQUEST);
            verifier.String.equals(exception.getReason(), String.format("Customer with %s id does not exist.", invalidId));
            verifier.verify();
        }
    }

    @Test
    public void addCustomerWithDuplicateEmailReturnsBadRequestInsteadOfCrashing() {
        String email = RandomStringUtils.randomAlphabetic(5).concat("@mail.mail");
        Customer existing = new Customer();
        existing.setEmail(email);

        Customer duplicate = new Customer();
        duplicate.setEmail(email);

        when(customerRepo.findByEmail(email)).thenReturn(existing);

        // addCustomer used to call HttpStatus.valueOf(<a sentence>), which throws
        // IllegalArgumentException at runtime instead of returning this 400.
        ResponseEntity<String> response = customerController.addCustomer(duplicate);

        Verifier verifier = new Verifier();
        verifier.Object.equals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        verifier.String.equals(response.getBody(), String.format("Customer with %s email already exists.", email));
        verifier.verify();
    }
}
