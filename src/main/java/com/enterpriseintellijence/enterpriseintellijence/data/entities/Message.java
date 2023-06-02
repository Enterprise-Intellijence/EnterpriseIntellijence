package com.enterpriseintellijence.enterpriseintellijence.data.entities;

import com.enterpriseintellijence.enterpriseintellijence.dto.enums.MessageStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(length = 36, nullable = false, updatable = false)
    private String id;
    private String text;

    @Column(name = "message_date")
    private LocalDateTime messageDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_status")
    private MessageStatus messageStatus;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "send_user")
    private  User sendUser;

    @ManyToOne
    @JoinColumn(name = "received_user")
    private User receivedUser;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="offer_id")
    private Offer offer;

}
