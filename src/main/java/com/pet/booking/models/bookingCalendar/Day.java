package com.pet.booking.models.bookingCalendar;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Entity
@Data
@Accessors(chain = true)
public class Day {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private Date date;
    @ElementCollection
    @CollectionTable(name = "listOfServices")
    private List<BookingEntry> bookingEntries;
}
