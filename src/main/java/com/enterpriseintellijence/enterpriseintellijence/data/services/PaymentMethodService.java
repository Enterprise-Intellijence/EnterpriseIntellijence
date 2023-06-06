package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.PaymentMethodDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.PaymentMethodBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.PaymentMethodCreateDTO;
import org.springframework.data.domain.Page;

public interface PaymentMethodService {

        PaymentMethodDTO createPaymentMethod(PaymentMethodCreateDTO paymentMethodCreateDTO) throws IllegalAccessException;
        PaymentMethodDTO replacePaymentMethod(String id, PaymentMethodDTO paymentMethodDTO) throws IllegalAccessException;
        PaymentMethodDTO updatePaymentMethod(String id, PaymentMethodDTO paymentMethodDTO) throws IllegalAccessException;
        void deletePaymentMethod(String id) throws IllegalAccessException;
        PaymentMethodDTO getPaymentMethodById(String id) throws IllegalAccessException;

        Page<PaymentMethodBasicDTO> getMyPaymentMethods(int page, int size);
}