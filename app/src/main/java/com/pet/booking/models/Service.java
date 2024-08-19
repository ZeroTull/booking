package com.pet.booking.models;

import com.pet.booking.enums.ServiceTypeName;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "SERVICE")
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long service_id;

    @Enumerated(EnumType.STRING)
    @Column(name = "SERVICE_TYPE_NAME")
    private ServiceTypeName serviceTypeName;

    @Column
    private int price;

    @Column
    private int durationInMinutes;
}
