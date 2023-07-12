package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.dto.basics.ProductBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.OfferState;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class OfferDTO {

    @NotNull
    private String id;

    @NotNull
    private CustomMoneyDTO amount;

    @Past
    private LocalDateTime creationTime;

    @NotNull
    private OfferState state;

    @NotNull
    private UserBasicDTO offerer;

    @NotNull
    private ProductBasicDTO product;

    //private OrderBasicDTO order;



}
