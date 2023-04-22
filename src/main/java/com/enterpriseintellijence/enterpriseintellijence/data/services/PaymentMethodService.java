package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.PaymentMethodDTO;
import org.springframework.http.ResponseEntity;

public interface PaymentMethodService {

        PaymentMethodDTO createPaymentMethod(PaymentMethodDTO paymentMethodDTO);
        ResponseEntity<PaymentMethodDTO> replacePaymentMethod(String id, PaymentMethodDTO paymentMethodDTO);
        ResponseEntity<PaymentMethodDTO> updatePaymentMethod(String id, PaymentMethodDTO paymentMethodDTO);
        ResponseEntity<PaymentMethodDTO> deletePaymentMethod(String id);
        ResponseEntity<PaymentMethodDTO> getPaymentMethod(String id);

}
