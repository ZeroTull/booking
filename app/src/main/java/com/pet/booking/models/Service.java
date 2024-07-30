package com.pet.booking.models;

import com.pet.booking.enums.ServiceTypeName;
import jakarta.persistence.*;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

@Data
@Entity
@Table(name = "service")
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long service_id;

    @Enumerated(EnumType.STRING)
    @Column(name = "serviceTypeName")
    private ServiceTypeName serviceTypeName;

    @Column
    @NotNull
    private int price;

    @Column
    @NotNull
    private int durationInMinutes;
}
