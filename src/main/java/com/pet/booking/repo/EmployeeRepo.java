package com.pet.booking.repo;

import com.pet.booking.models.Employee;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EmployeeRepo extends CrudRepository<Employee, Long> {
    Employee findByEmail(String email);

    List<Employee> findAllByOrderByIdDesc();
}
