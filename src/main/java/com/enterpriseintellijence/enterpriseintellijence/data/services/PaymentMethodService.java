package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.PaymentMethodDTO;

public interface PaymentMethodService {

        PaymentMethodDTO createPaymentMethod(PaymentMethodDTO paymentMethodDTO) throws IllegalAccessException;
        PaymentMethodDTO replacePaymentMethod(String id, PaymentMethodDTO paymentMethodDTO) throws IllegalAccessException;
        PaymentMethodDTO updatePaymentMethod(String id, PaymentMethodDTO paymentMethodDTO) throws IllegalAccessException;
        void deletePaymentMethod(String id) throws IllegalAccessException;
        PaymentMethodDTO getPaymentMethodById(String id) throws IllegalAccessException;
}