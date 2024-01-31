package com.pet.booking.repo;

import com.pet.booking.models.bookingCalendar.Day;
import org.springframework.data.repository.CrudRepository;

public interface DayRepo extends CrudRepository<Day, Long> {
}
