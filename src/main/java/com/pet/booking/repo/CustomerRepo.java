package com.pet.booking.repo;

import com.pet.booking.models.Customer;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepo extends CrudRepository<Customer, Long> {
    Customer findByEmail(String email);
}
