package com.enterpriseintellijence.enterpriseintellijence.data.entities;

import com.enterpriseintellijence.enterpriseintellijence.dto.enums.EntertainmentType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Table(name = "entertainment")
@Entity
@PrimaryKeyJoinColumn(name = "product_id")
public class Entertainment extends Product{
    // TODO: 07/05/2023 non sono riuscito ad individuare per ora altre caratteristiche tipiche per sta classe

    @Enumerated(EnumType.STRING)
    @Column(name="entertainment_type",nullable = false)
    private EntertainmentType entertainmentType;
}
