package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.PaymentMethod;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.PaymentMethodRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.UserRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.PaymentMethodDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.PaymentMethodBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.PaymentMethodCreateDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.UserRole;
import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;
import com.enterpriseintellijence.enterpriseintellijence.security.JwtContextUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentMethodServiceImp implements PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final JwtContextUtils jwtContextUtils;

    @Override
    public PaymentMethodDTO createPaymentMethod(PaymentMethodCreateDTO paymentMethodCreateDTO) throws IllegalAccessException {
        PaymentMethod paymentMethod = modelMapper.map(paymentMethodCreateDTO,PaymentMethod.class);

        User loggedUser = jwtContextUtils.getUserLoggedFromContext();

/*        if (!loggedUser.getId().equals(paymentMethod.getDefaultUser().getId())) {
            throw new IllegalAccessException("User cannot create payment method");
        }*/

/*        paymentMethod.setCreditCard(paymentMethodDTO.getCreditCard());
        paymentMethod.setOwner(paymentMethodDTO.getOwner());
        paymentMethod.setExpiryDate(paymentMethodDTO.getExpiryDate());*/
        paymentMethod.setOwnerUser(loggedUser);

        if(paymentMethod.isDefault()){
            for(PaymentMethod paymentMethod1:loggedUser.getPaymentMethods()) {
                if (paymentMethod1.isDefault()) {
                    paymentMethod1.setDefault(false);
                    paymentMethodRepository.save(paymentMethod1);
                }
            }
        }

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
        if(patch.isDefault()){
            for(PaymentMethod paymentMethod1:loggedUser.getPaymentMethods()) {
                if (paymentMethod1.isDefault()) {
                    paymentMethod1.setDefault(false);
                    paymentMethodRepository.save(paymentMethod1);
                }
            }
            paymentMethod.setDefault(true);
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

    @Override
    public Page<PaymentMethodBasicDTO> getMyPaymentMethods(int page, int size) {
        User user = jwtContextUtils.getUserLoggedFromContext();
        Page<PaymentMethod> paymentMethods = new PageImpl<PaymentMethod>(user.getPaymentMethods(), PageRequest.of(page,size),user.getPaymentMethods().size());
        List<PaymentMethodBasicDTO> collect = paymentMethods.stream().map(s->modelMapper.map(s, PaymentMethodBasicDTO.class)).collect(Collectors.toList());

        return new PageImpl<>(collect);
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
