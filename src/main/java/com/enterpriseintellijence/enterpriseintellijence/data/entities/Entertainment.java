package com.enterpriseintellijence.enterpriseintellijence.data.entities;

import com.enterpriseintellijence.enterpriseintellijence.dto.enums.EntertainmentLanguage;
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

    @Enumerated(EnumType.STRING)
    @Column(name="entertainment_language",nullable = false)
    private EntertainmentLanguage entertainmentLanguage;
}
