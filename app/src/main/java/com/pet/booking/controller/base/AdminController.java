package com.pet.booking.controller.base;

import com.pet.booking.models.Service;
import com.pet.booking.repo.ServiceRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.pet.booking.controller.base.ApiDefinition.ADMIN_RESOURCE_ROOT;

@Slf4j
@RestController
@RequestMapping(value = ADMIN_RESOURCE_ROOT)
public class AdminController {
    @Autowired
    private ServiceRepo serviceRepo;

    @PostMapping(path = "/addServiceType", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addServiceType(@RequestBody final Service dto) {
        Service serviceType = serviceRepo.findByServiceTypeName(dto.getServiceTypeName());

        if (serviceType != null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(String.format("Service type '%s' already exists.", dto.getServiceTypeName()));
        }

        serviceRepo.save(dto);
        log.info(String.format("Created %s service type.", dto.getServiceTypeName()));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
