package com.pet.booking.models.bookingCalendar;

import com.pet.booking.models.Service;
import jakarta.persistence.*;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "APPOINTMENT")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "APPOINTMENT_ID")
    private long id;

    @Column
    @NotNull
    private int employeeId;

    @Column
    @NotNull
    private String customerName;

    @Column
    @NotNull
    private String customerEmail;

    @Column
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;

    //TODO - update to use existing in db services, by Id. Create such services via Liquibase
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Service service;

    private boolean isActive = true;
}
