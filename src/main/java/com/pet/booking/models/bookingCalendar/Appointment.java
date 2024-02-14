package com.pet.booking.models.bookingCalendar;

import com.pet.booking.enums.ServiceType;
import jakarta.persistence.*;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "appointment")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private LocalDateTime date;

    @Column(name = "serviceType")
    @NotNull
    @Enumerated(EnumType.STRING)
    private ServiceType serviceType;

    private boolean isActive = true;
}
