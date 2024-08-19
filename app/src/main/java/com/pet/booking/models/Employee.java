package com.pet.booking.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "EMPLOYEE")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    //private File photo;  //todo - check how to store and how to return via api - separate table for photos

    @Column
    private String firstName;

    @Column
    private String lastName;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "SERVICE_ID")
    @CollectionTable(name = "SERVICES_LIST")
    private List<Service> services;  //todo - validate in db //add validation onto 'add' endpoint via fromString or smth like that

    @Column(unique = true)
    private String email;

    @Column
    private String password;

    @Column
    private String phoneNumber;

    @Column
    boolean isAdmin = false;
}
