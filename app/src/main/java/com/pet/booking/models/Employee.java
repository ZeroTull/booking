package com.pet.booking.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    //private File photo;  //todo - check how to store and how to return via api - separate table for photos

    @Column
    @NotNull
    private String firstName;

    @Column
    @NotNull
    private String lastName;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "service_id")
    @CollectionTable(name = "services_list")
    private List<Service> services;  //todo - validate in db //add validation onto 'add' endpoint via fromString or smth like that

    @Column(unique = true)
    @NotNull
    private String email;

    @Column
    @NotNull
    private String password;

    @Column
    @NotNull
    private String phoneNumber;

    @Column
    boolean isAdmin = false;
}
