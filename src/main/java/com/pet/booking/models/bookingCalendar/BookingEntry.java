package com.pet.booking.models.bookingCalendar;

import jakarta.persistence.*;

@Entity
@Table(name = "booking_entry")
public class BookingEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
}
