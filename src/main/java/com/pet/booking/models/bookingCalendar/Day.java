package com.pet.booking.models.bookingCalendar;

import com.pet.booking.enums.JobType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Entity
@Data
@Accessors(chain = true)
public class Day {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private Date date;
    private JobType jobType;
    private int price;
    private int bookedEmployee;
    private boolean isActive; // = getDate().after(new Date());
}
