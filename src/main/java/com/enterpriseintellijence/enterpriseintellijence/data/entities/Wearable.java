package com.enterpriseintellijence.enterpriseintellijence.data.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Data
@NoArgsConstructor
@Table(name = "wearable")
@Entity
@PrimaryKeyJoinColumn(name = "product_id")
public class Wearable extends Product{
    private String size;
    private String colour; //TODO: da vedere se utilizzare o meno un ENUM
    private String type;



}
