package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Transaction;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.TransactionRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.TransactionDTO;
import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImp implements TransactionService{

    private final TransactionRepository transactionRepository;
    private final ModelMapper modelMapper;
    public TransactionDTO createTransaction(TransactionDTO transactionDTO) {
        Transaction transaction = mapToEntity(transactionDTO);
        transaction = transactionRepository.save(transaction);
        return mapToDTO(transaction);
    }

    public TransactionDTO replaceTransaction(String id, TransactionDTO transactionDTO) {
        //todo: da fare perchè non c'è l'allacciamento tra Transaction e User
        return transactionDTO;
    }

    public TransactionDTO updateTransaction(String id, TransactionDTO patch)  {
        TransactionDTO transaction = mapToDTO(transactionRepository.findById(id).orElseThrow(EntityNotFoundException::new));
        transaction.setAmount(patch.getAmount());
        transaction.setPaymentMethod(patch.getPaymentMethod());
        transactionRepository.save(mapToEntity(transaction));
        return transaction;
    }

    public TransactionDTO deleteTransaction(String id) {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        transactionRepository.deleteById(id);
        return mapToDTO(transaction);
    }

    public TransactionDTO transactionById(String id) {
       Transaction transaction = transactionRepository.findById(id).orElseThrow(EntityNotFoundException::new);
       return mapToDTO(transaction);
    }

    public Iterable<TransactionDTO> findAll() {
        return transactionRepository.findAll().stream().map(s -> mapToDTO(s)).collect(Collectors.toList());
    }


    private void throwOnIdMismatch(String id, TransactionDTO transactionDTO){
        if(transactionDTO.getId() != null && !transactionDTO.getId().equals(id))
            throw new IdMismatchException();
    }

    public Transaction mapToEntity(TransactionDTO transactionDTO){return modelMapper.map(transactionDTO,Transaction.class);}
    public TransactionDTO mapToDTO(Transaction transaction){return modelMapper.map(transaction,TransactionDTO.class);}
}
