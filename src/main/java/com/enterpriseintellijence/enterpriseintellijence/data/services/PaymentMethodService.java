package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.PaymentMethodDTO;
import org.springframework.http.ResponseEntity;

public interface PaymentMethodService {

        PaymentMethodDTO createPaymentMethod(PaymentMethodDTO paymentMethodDTO);
        PaymentMethodDTO replacePaymentMethod(String id, PaymentMethodDTO paymentMethodDTO) throws IllegalAccessException;
        PaymentMethodDTO updatePaymentMethod(String id, PaymentMethodDTO paymentMethodDTO) throws IllegalAccessException;
        PaymentMethodDTO deletePaymentMethod(String id);
        PaymentMethodDTO getPaymentMethodById(String id) throws IllegalAccessException;
}