package com.pet.booking.controller.calendar;

import com.pet.booking.models.bookingCalendar.Day;
import com.pet.booking.repo.DayRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.pet.booking.controller.base.ApiDefinition.DAY_RESOURCE_ROOT;

@Slf4j
@RestController
@RequestMapping(value = DAY_RESOURCE_ROOT)
public class DayController {
    @Autowired
    DayRepo dayRepo;

    @GetMapping(path = "/getAllDays")
    public Iterable<Day> getAllDays() {
        return dayRepo.findAll();
    }

    @PostMapping(path = "/addDay")
    public void addDay(@RequestBody final Day day) {
        dayRepo.save(day);
    }
}
