package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.PaymentMethod;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.PaymentMethodRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.UserRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.PaymentMethodDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.UserRole;
import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;
import com.enterpriseintellijence.enterpriseintellijence.security.JwtContextUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentMethodServiceImp implements PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final JwtContextUtils jwtContextUtils;

    @Override
    public PaymentMethodDTO createPaymentMethod(PaymentMethodDTO paymentMethodDTO) throws IllegalAccessException {
        PaymentMethod paymentMethod = mapToEntity(paymentMethodDTO);

        User loggedUser = jwtContextUtils.getUserLoggedFromContext();

/*        if (!loggedUser.getId().equals(paymentMethod.getDefaultUser().getId())) {
            throw new IllegalAccessException("User cannot create payment method");
        }*/

        paymentMethod.setCreditCard(paymentMethodDTO.getCreditCard());
        paymentMethod.setOwner(paymentMethodDTO.getOwner());
        paymentMethod.setExpiryDate(paymentMethodDTO.getExpiryDate());
        paymentMethod.setOwnerUser(loggedUser);

        paymentMethod = paymentMethodRepository.save(paymentMethod);
        return mapToDTO(paymentMethod);
    }

    @Override
    public PaymentMethodDTO replacePaymentMethod(String id, PaymentMethodDTO paymentMethodDTO) throws IllegalAccessException {
       /* throwOnIdMismatch(id, paymentMethodDTO);

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

        newPaymentMethod = paymentMethodRepository.save(newPaymentMethod);*/
        return updatePaymentMethod(id,paymentMethodDTO);
    }

    @Override
    public PaymentMethodDTO updatePaymentMethod(String id, PaymentMethodDTO patch) throws IllegalAccessException {
        throwOnIdMismatch(id,patch);
        PaymentMethod paymentMethod = paymentMethodRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();

        if (loggedUser.getRole().equals(UserRole.USER) && !paymentMethod.getOwnerUser().getId().equals(loggedUser.getId())) {
            throw new IllegalAccessException("User cannot update payment method");
        }

        if (patch.getCreditCard() != null && !paymentMethod.getCreditCard().equals(patch.getCreditCard())) {
            paymentMethod.setCreditCard(patch.getCreditCard());
        }

        if (patch.getExpiryDate() != null && !paymentMethod.getExpiryDate().equals(patch.getExpiryDate())) {
            paymentMethod.setExpiryDate(patch.getExpiryDate());
        }

        if (patch.getOwner() != null && !paymentMethod.getOwner().equals(patch.getOwner())) {
            paymentMethod.setOwner(patch.getOwner());
        }


        paymentMethodRepository.save(paymentMethod);
        return mapToDTO(paymentMethod);
    }

    @Override
    public void deletePaymentMethod(String id) throws IllegalAccessException {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if(loggedUser.getRole().equals(UserRole.USER) && !paymentMethod.getOwnerUser().getId().equals(loggedUser.getId()))
            throw new IllegalAccessException("Cannot delete payment method");

        paymentMethodRepository.deleteById(id);
    }

    @Override
    public PaymentMethodDTO getPaymentMethodById(String id) throws IllegalAccessException {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();

        if (loggedUser.getRole().equals(UserRole.USER) && !loggedUser.getId().equals(paymentMethod.getOwnerUser().getId())) {
            throw new IllegalAccessException("User cannot get payment method");
        }

        return mapToDTO(paymentMethod);
    }

    public PaymentMethod mapToEntity(PaymentMethodDTO paymentMethodDTO) {
        return modelMapper.map(paymentMethodDTO, PaymentMethod.class);
    }
    public PaymentMethodDTO mapToDTO(PaymentMethod paymentMethod) {
        return modelMapper.map(paymentMethod, PaymentMethodDTO.class);
    }

    private void throwOnIdMismatch(String id, PaymentMethodDTO paymentMethodDTO) {
        if (paymentMethodDTO.getId() != null && !paymentMethodDTO.getId().equals(id))
            throw new IdMismatchException();
    }
}
