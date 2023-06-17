package com.enterpriseintellijence.enterpriseintellijence.data.entities;


import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Visibility;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductCategory {
    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    @Column(nullable = false)
    private String primaryCat;

    @Column(nullable = false)
    private String secondaryCat;

    @Column(nullable = false)
    private String tertiaryCat;

    @Column()
    @Enumerated(EnumType.STRING)
    private Visibility visibility;

    @OneToMany(mappedBy = "productCategory",fetch = FetchType.LAZY)
    private List<Product> products;


}
