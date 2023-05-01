package com.enterpriseintellijence.enterpriseintellijence.data.entities;

import com.enterpriseintellijence.enterpriseintellijence.dto.enums.ClothingType;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.ProductGender;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Table(name = "wearable")
@Entity
@PrimaryKeyJoinColumn(name = "product_id")
public class Clothing extends Product{
    @Enumerated(EnumType.STRING)
    @Column(name="product_gender",nullable = false)
    private ProductGender productGender;

    private String size;

    private String colour;

    @Enumerated(EnumType.STRING)
    @Column(name="clothing_type",nullable = false)
    private ClothingType clothingType;



}
