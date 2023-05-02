package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.PaymentMethod;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.PaymentMethodRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.PaymentMethodDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserDTO;
import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentMethodServiceImp implements PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;

    @Override
    public PaymentMethodDTO createPaymentMethod(PaymentMethodDTO paymentMethodDTO) {
        PaymentMethod paymentMethod = mapToEntity(paymentMethodDTO);

        paymentMethod.setCreditCard(paymentMethodDTO.getCreditCard());
        paymentMethod.setOwner(paymentMethod.getOwner());
        paymentMethod.setExpiryDate(paymentMethodDTO.getExpiryDate());
        paymentMethod.setOwnerUser(mapToEntity(paymentMethodDTO.getOwnerUser()));
        paymentMethod.setDefaultUser(mapToEntity(paymentMethodDTO.getDefaultUser()));

        paymentMethod = paymentMethodRepository.save(paymentMethod);
        return mapToDTO(paymentMethod);
    }

    @Override
    public PaymentMethodDTO replacePaymentMethod(String id, PaymentMethodDTO paymentMethodDTO) throws IllegalAccessException {
        throwOnIdMismatch(id, paymentMethodDTO);

        PaymentMethod oldPaymentMethod = paymentMethodRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        PaymentMethod newPaymentMethod = mapToEntity(paymentMethodDTO);

        UserDTO requestingUser = userService.findUserFromContext()
            .orElseThrow(EntityNotFoundException::new);

        if (!requestingUser.getId().equals(oldPaymentMethod.getDefaultUser().getId())) {
            throw new IllegalAccessException("User cannot change payment method");
        }

        if (!requestingUser.getId().equals(newPaymentMethod.getDefaultUser().getId())) {
            throw new IllegalAccessException("User cannot change payment method");
        }

        newPaymentMethod = paymentMethodRepository.save(newPaymentMethod);
        return mapToDTO(newPaymentMethod);
    }

    @Override
    public PaymentMethodDTO updatePaymentMethod(String id, PaymentMethodDTO patch) throws IllegalAccessException {

        PaymentMethodDTO paymentMethodDTO = getPaymentMethodById(id);

        UserDTO userDTO = userService.findUserFromContext()
                .orElseThrow(EntityNotFoundException::new);

        if (userDTO.getId().equals(paymentMethodDTO.getDefaultUser().getId())) {
            throw new IllegalAccessException("User cannot update payment method");
        }

        if (patch.getCreditCard() != null) {
            paymentMethodDTO.setCreditCard(patch.getCreditCard());
        }

        if (patch.getExpiryDate() != null) {
            paymentMethodDTO.setExpiryDate(patch.getExpiryDate());
        }

        if (patch.getOwner() != null) {
            paymentMethodDTO.setOwner(patch.getOwner());
        }


        paymentMethodDTO = mapToDTO(paymentMethodRepository.save(mapToEntity(paymentMethodDTO)));
        return paymentMethodDTO;
    }

    @Override
    public PaymentMethodDTO deletePaymentMethod(String id) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        paymentMethodRepository.delete(paymentMethod);
        return mapToDTO(paymentMethod);
    }

    @Override
    public PaymentMethodDTO getPaymentMethodById(String id) throws IllegalAccessException {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        PaymentMethodDTO paymentMethodDTO = mapToDTO(paymentMethod);

        UserDTO userDTO = userService.findUserFromContext()
                .orElseThrow(EntityNotFoundException::new);

        if (!userDTO.getId().equals(paymentMethodDTO.getDefaultUser().getId())) {
            throw new IllegalAccessException("User cannot get payment method");
        }

        return paymentMethodDTO;
    }

    public PaymentMethod mapToEntity(PaymentMethodDTO paymentMethodDTO) {
        return modelMapper.map(paymentMethodDTO, PaymentMethod.class);
    }
    public PaymentMethodDTO mapToDTO(PaymentMethod paymentMethod) {
        return modelMapper.map(paymentMethod, PaymentMethodDTO.class);
    }
    public User mapToEntity(UserDTO userDTO){return modelMapper.map(userDTO, User.class);}

    private void throwOnIdMismatch(String id, PaymentMethodDTO paymentMethodDTO) {
        if (paymentMethodDTO.getId() != null && !paymentMethodDTO.getId().equals(id))
            throw new IdMismatchException();
    }
}
