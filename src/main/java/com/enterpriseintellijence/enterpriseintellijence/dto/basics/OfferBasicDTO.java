package com.enterpriseintellijence.enterpriseintellijence.dto.basics;

import com.enterpriseintellijence.enterpriseintellijence.dto.*;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.OfferState;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class OfferBasicDTO {

    @NotNull
    private String id;

    @NotNull
    private CustomMoneyDTO amount;

    @NotNull
    private OfferState state;

    private LocalDateTime creationTime;

}