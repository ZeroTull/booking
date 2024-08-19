package com.pet.booking.models.bookingCalendar;

import com.pet.booking.models.Service;
import jakarta.persistence.*;
import lombok.Data;
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
    private int employeeId;
    @Column
    private String customerName;
    @Column
    private String customerEmail;
    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;
    //TODO - update to use existing in db services, by Id. Create such services via Liquibase
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Service service;
    @Column
    private boolean isActive = true;
}
