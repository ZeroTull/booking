package com.pet.booking.dto;

import com.pet.booking.models.Service;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class AppointmentDTO {
    private long id;
    private EmployeeDTO employee;
    private CustomerDTO customer;
    private LocalDateTime dateTime;
    private Service service;
    private boolean active;
}
