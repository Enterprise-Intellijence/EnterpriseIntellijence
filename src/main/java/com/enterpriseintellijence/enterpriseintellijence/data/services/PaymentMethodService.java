package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.PaymentMethodDTO;
import org.springframework.http.ResponseEntity;

public interface PaymentMethodService {

        PaymentMethodDTO createPaymentMethod(PaymentMethodDTO paymentMethodDTO);
        ResponseEntity<PaymentMethodDTO> replacePaymentMethod(PaymentMethodDTO paymentMethodDTO);
        ResponseEntity<PaymentMethodDTO> updatePaymentMethod(PaymentMethodDTO paymentMethodDTO);
        ResponseEntity<PaymentMethodDTO> deletePaymentMethod(String id);
        ResponseEntity<PaymentMethodDTO> getPaymentMethod(String id);

}
