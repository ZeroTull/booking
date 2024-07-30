package com.pet.booking.dto;

import com.pet.booking.models.Service;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class EmployeeDTO {
    private long id;
    private String firstName;
    private String lastName;
    private List<Service> services;  //todo - validate in db //add validaion onto 'add' entpoint via fromString or smth like that
    private String email;
    private String phoneNumber;
}
