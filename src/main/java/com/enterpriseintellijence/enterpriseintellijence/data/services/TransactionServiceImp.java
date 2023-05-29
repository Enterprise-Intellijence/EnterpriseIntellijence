package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.core.services.ProcessSaleServiceImp;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.*;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.embedded.CustomMoney;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.OrderRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.PaymentMethodRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.TransactionRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.TransactionDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.TransactionCreateDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Availability;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.OrderState;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.UserRole;
import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;
import com.enterpriseintellijence.enterpriseintellijence.security.JwtContextUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImp implements TransactionService{

    private final TransactionRepository transactionRepository;
    private final OrderRepository orderRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final ProcessSaleServiceImp processSaleServiceImp;
    private final ModelMapper modelMapper;
    private final JwtContextUtils jwtContextUtils;
    private final Clock clock;

    public TransactionDTO createTransaction(TransactionCreateDTO transactionDTO) throws IllegalAccessException {
        // TODO: 28/05/2023 forse più opportuno ricevere come parametri id order e id paymentmethod
        Order order = orderRepository.findById(transactionDTO.getOrder().getId()).orElseThrow(EntityNotFoundException::new);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        PaymentMethod paymentMethod = paymentMethodRepository.findById(transactionDTO.getPaymentMethod().getId()).orElseThrow(EntityNotFoundException::new);


        if(!order.getUser().getId().equals(loggedUser.getId()))
            throw new IllegalAccessException("Cannot create transaction");

        checkCardOwnership(loggedUser,paymentMethod);

        Transaction transaction = processSaleServiceImp.payProduct(order,loggedUser,paymentMethod);

        // TODO: 28/05/2023 check cosa salva



        transaction = transactionRepository.save(transaction);
        return mapToDTO(transaction);
    }

    public TransactionDTO replaceTransaction(String id, TransactionDTO transactionDTO) throws IllegalAccessException {
        return updateTransaction(id,transactionDTO);
    }

    public TransactionDTO updateTransaction(String id, TransactionDTO patch) throws IllegalAccessException {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        //check sugli id
        throwOnIdMismatch(id,patch);

        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        //controllo che chi effettua l'operazione sia un admin o il proprietario della transazione
        if(loggedUser!= null && loggedUser.getRole().equals(UserRole.USER) && !transaction.getOrder().getUser().getId().equals(loggedUser.getId()))
            throw new IllegalAccessException("Cannot modify transaction");
        //controllo che il campo del metodo non sia null
        if(patch.getPaymentMethod()!=null){
            PaymentMethod paymentMethod = paymentMethodRepository.getReferenceById(patch.getPaymentMethod().getId());
            //controllo che il proprietario della carta sia nei metodi di pagamento dell'utente
            checkCardOwnership(transaction.getOrder().getUser(),paymentMethod);
            //verifico se il metodo di pagamento è effettivamente diverso
            if(!transaction.getPaymentMethod().equals(paymentMethod)) {
                transaction.setPaymentMethod(paymentMethod);
                transaction.getOrder().setState(OrderState.PURCHASED);
                transaction.getOrder().getProduct().setAvailability(Availability.UNAVAILABLE);
                transactionRepository.save(transaction);
            }
        }

        return mapToDTO(transaction);
    }

    public void deleteTransaction(String id) throws IllegalAccessException {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        //controllo che chi effettua l'operazione sia un admin o il proprietario della transazione
        if(loggedUser.getRole().equals(UserRole.USER) && !transaction.getOrder().getUser().getId().equals(loggedUser.getId()))
            throw new IllegalAccessException("Cannot modify transaction");
        transactionRepository.deleteById(id);
    }

    public TransactionDTO transactionById(String id) {
       Transaction transaction = transactionRepository.findById(id).orElseThrow(EntityNotFoundException::new);
       return mapToDTO(transaction);
    }

    public Iterable<TransactionDTO> findAll() {
        return transactionRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }


    private void throwOnIdMismatch(String id, TransactionDTO transactionDTO){
        if(transactionDTO.getId() != null && !transactionDTO.getId().equals(id))
            throw new IdMismatchException();
    }

    public Transaction mapToEntity(TransactionDTO transactionDTO){return modelMapper.map(transactionDTO,Transaction.class);}
    public TransactionDTO mapToDTO(Transaction transaction){return modelMapper.map(transaction,TransactionDTO.class);}

    private boolean checkCardOwnership(User user, PaymentMethod paymentMethod) throws IllegalAccessException {
        if(!user.getPaymentMethods().contains(paymentMethod))
            throw new IllegalAccessException("Cannot use this credit card");

        return true;

    }
}
