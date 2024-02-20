package com.pet.booking.repo;

import com.pet.booking.enums.ServiceTypeName;
import com.pet.booking.models.Service;
import org.springframework.data.repository.CrudRepository;

public interface ServiceRepo extends CrudRepository<Service, Long> {
    Service findByServiceTypeName(ServiceTypeName name);
}
