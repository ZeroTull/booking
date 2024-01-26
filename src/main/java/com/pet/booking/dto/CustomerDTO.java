package com.pet.booking.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CustomerDTO {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
}
