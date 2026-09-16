package com.pet.booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

// @SpringBootApplication already includes @EnableAutoConfiguration (plus @ComponentScan and
// @Configuration) -- the explicit one was redundant.
@EnableJpaRepositories
@SpringBootApplication
public class BookingApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookingApplication.class, args);
	}
}
