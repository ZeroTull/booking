package com.pet.booking.repo;

import com.pet.booking.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepo extends JpaRepository<Employee, Long> {
    Employee findByEmail(String email);
}
