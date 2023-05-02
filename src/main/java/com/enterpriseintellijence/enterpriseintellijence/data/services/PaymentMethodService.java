package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.PaymentMethodDTO;
import org.springframework.http.ResponseEntity;

public interface PaymentMethodService {

        PaymentMethodDTO createPaymentMethod(PaymentMethodDTO paymentMethodDTO);
        PaymentMethodDTO replacePaymentMethod(String id, PaymentMethodDTO paymentMethodDTO);
        PaymentMethodDTO updatePaymentMethod(String id, PaymentMethodDTO paymentMethodDTO);
        PaymentMethodDTO deletePaymentMethod(String id);
        PaymentMethodDTO getPaymentMethod(String id);
}