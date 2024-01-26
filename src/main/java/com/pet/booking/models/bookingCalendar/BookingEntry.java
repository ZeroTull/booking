package com.pet.booking.models.bookingCalendar;

import com.pet.booking.enums.ServiceType;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table(name = "booking_entry")
public class BookingEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private int employeeId;
    @Column
    private int customerId;
    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private Date dateAndTime;
    @Enumerated(EnumType.STRING)
    @Column(name = "serviceType")
    private ServiceType serviceType;
}
