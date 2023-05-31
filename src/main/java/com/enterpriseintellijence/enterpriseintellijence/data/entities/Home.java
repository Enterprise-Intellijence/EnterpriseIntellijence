package com.enterpriseintellijence.enterpriseintellijence.data.entities;

import com.enterpriseintellijence.enterpriseintellijence.dto.enums.*;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Table(name = "home")
@Entity
@PrimaryKeyJoinColumn(name = "product_id")
public class Home extends Product{

    @Enumerated(EnumType.STRING)
    @Column(name="colour",nullable = false)
    private Colour colour;

    @Enumerated(EnumType.STRING)
    @Column(name="home_size",nullable = false)
    private HomeSize homeSize;
    @Enumerated(EnumType.STRING)
    @Column(name="home_material",nullable = false)
    private HomeMaterial homeMaterial;

}
