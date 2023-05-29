package com.enterpriseintellijence.enterpriseintellijence.data.entities;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.embedded.CustomMoney;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.TransactionState;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.joda.money.Money;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "transaction")
@Entity

public class Transaction {
    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    @Column(name = "creation_date")
    private LocalDateTime creationTime;

    @Embedded
    private CustomMoney amount;

    @Enumerated(EnumType.STRING)
    private TransactionState transactionState;

    @ManyToOne()
    @JoinColumn(name = "payment_method")
    private PaymentMethod paymentMethod;

    @OneToOne(mappedBy = "transaction")
    private Order order;
}
