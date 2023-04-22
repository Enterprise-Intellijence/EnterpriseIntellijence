package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.PaymentMethodDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PaymentMethodServiceImp implements PaymentMethodService {

    @Override
    public PaymentMethodDTO createPaymentMethod(PaymentMethodDTO paymentMethodDTO) {
        //TODO: Implement this method
        return null;
    }

    @Override
    public ResponseEntity<PaymentMethodDTO> replacePaymentMethod(String id, PaymentMethodDTO paymentMethodDTO) {
        //TODO: Implement this method

        return null;
    }

    @Override
    public ResponseEntity<PaymentMethodDTO> updatePaymentMethod(String id, PaymentMethodDTO paymentMethodDTO) {
        //TODO: Implement this method
        return null;
    }

    @Override
    public ResponseEntity<PaymentMethodDTO> deletePaymentMethod(String id) {
        //TODO: Implement this method
        return null;
    }

    @Override
    public ResponseEntity<PaymentMethodDTO> getPaymentMethod(String id) {
        //TODO: Implement this method
        return null;
    }
}
