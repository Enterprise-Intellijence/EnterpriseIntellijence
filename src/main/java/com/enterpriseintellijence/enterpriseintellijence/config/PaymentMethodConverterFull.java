package com.enterpriseintellijence.enterpriseintellijence.config;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.PaymentMethod;
import com.enterpriseintellijence.enterpriseintellijence.dto.PaymentMethodDTO;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Service;


@Service
public class PaymentMethodConverterFull extends AbstractConverter<PaymentMethod, PaymentMethodDTO> {

    @Override
    protected PaymentMethodDTO convert(PaymentMethod paymentMethod) {
        if(paymentMethod == null)
            return null;
        return PaymentMethodDTO.builder()
                .id(paymentMethod.getId())
                .expiryDate(paymentMethod.getExpiryDate())
                .isDefault(paymentMethod.isDefault())
                .owner(paymentMethod.getOwner())
                .creditCard("**** **** **** "+paymentMethod.getCreditCard().substring(15,19))
                .build();

    }
}