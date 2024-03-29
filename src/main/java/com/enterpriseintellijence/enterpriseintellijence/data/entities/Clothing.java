package com.enterpriseintellijence.enterpriseintellijence.data.entities;

import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Colour;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.ProductGender;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Table(name = "clothing")
@Entity
@PrimaryKeyJoinColumn(name = "product_id")
public class Clothing extends Product{
    @Enumerated(EnumType.STRING)
    @Column(name="product_gender",nullable = false)
    private ProductGender productGender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="size",nullable = false)
    private Size clothingSize;

    @Enumerated(EnumType.STRING)
    @Column(name="colour",nullable = false)
    private Colour colour;


}
