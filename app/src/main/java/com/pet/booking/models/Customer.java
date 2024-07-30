package com.pet.booking.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.antlr.v4.runtime.misc.NotNull;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "customer")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    @NotNull
    private String firstName;
    @Column
    @NotNull
    private String lastName;
    @Column(unique = true)
    @NotNull
    private String email;
    @Column
    @NotNull
    private String password;
    @Column
    @NotNull
    private String phoneNumber;
}
