package com.enterpriseintellijence.enterpriseintellijence.dto.basics;

import com.enterpriseintellijence.enterpriseintellijence.dto.*;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.OfferState;
import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class OfferBasicDTO {

    private String id;

    private CustomMoneyDTO amount;

    private OfferState state;

    private LocalDateTime creationTime;

}