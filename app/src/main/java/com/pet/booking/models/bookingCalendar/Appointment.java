package com.pet.booking.models.bookingCalendar;

import com.pet.booking.models.Customer;
import com.pet.booking.models.Employee;
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
    // Was a raw `int employeeId` with no FK -- nothing stopped an appointment from
    // referencing an employee id that didn't exist, and deleting an employee silently
    // orphaned their appointments instead of being blocked or cascaded.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMPLOYEE_ID")
    private Employee employee;
    // Was denormalized `customerName`/`customerEmail` strings instead of a real
    // relation -- same integrity gap as employeeId above.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID")
    private Customer customer;
    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;
    //TODO - update to use existing in db services, by Id. Create such services via Liquibase
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Service service;
    @Column
    private boolean isActive = true;
}
