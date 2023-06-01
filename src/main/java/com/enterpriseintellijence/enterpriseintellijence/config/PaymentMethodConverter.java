package com.enterpriseintellijence.enterpriseintellijence.config;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.PaymentMethod;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.PaymentMethodBasicDTO;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Service;

@Service
public class PaymentMethodConverter extends AbstractConverter<PaymentMethod, PaymentMethodBasicDTO> {

    @Override
    protected PaymentMethodBasicDTO convert(PaymentMethod paymentMethod) {
        if(paymentMethod == null)
            return null;
        return PaymentMethodBasicDTO.builder()
                .id(paymentMethod.getId())
                .creditCard("**** **** **** "+paymentMethod.getCreditCard().substring(15,19))
                .build();

    }
}
