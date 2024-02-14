package com.pet.booking.models;

import com.pet.booking.enums.ServiceType;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "service")
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long service_id;

    @Enumerated(EnumType.STRING)
    @Column(name = "serviceType")
    private ServiceType serviceType;

    private int price;
}
