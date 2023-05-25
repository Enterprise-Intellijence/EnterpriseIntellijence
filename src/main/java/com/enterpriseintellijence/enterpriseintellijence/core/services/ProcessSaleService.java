package com.enterpriseintellijence.enterpriseintellijence.core.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Offer;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Product;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.dto.OfferDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
public interface ProcessSaleService {

    Offer madeAnOffer(OfferDTO offerDTO, Product product, User loggedUser);
    Offer acceptOrRejectAnOffer(Offer offer, OfferDTO offerDTO, Product product, User loggedUser,boolean isAccepted);
}
