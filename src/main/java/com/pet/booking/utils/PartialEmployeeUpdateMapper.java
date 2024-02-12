package com.pet.booking.utils;


import com.pet.booking.models.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PartialEmployeeUpdateMapper {
    void partialEmployeeUpdate(@MappingTarget Employee target, Employee update);
}
