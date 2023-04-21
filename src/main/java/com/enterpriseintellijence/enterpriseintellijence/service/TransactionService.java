package com.enterpriseintellijence.enterpriseintellijence.service;

import com.enterpriseintellijence.enterpriseintellijence.dto.TransactionDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    public TransactionDTO createTransaction(TransactionDTO transactionDTO) {
        // TODO: 21/04/2023  
        return null;
    }

    public ResponseEntity<TransactionDTO> replaceTransaction(TransactionDTO transactionDTO) {
        // TODO: 21/04/2023  
        return null;
    }

    public ResponseEntity<TransactionDTO> updateTransaction(TransactionDTO transactionDTO) {
        // TODO: 21/04/2023  
        return null;
    }

    public void deleteTransaction(String id) {
        // TODO: 21/04/2023  
    }

    public ResponseEntity<TransactionDTO> transactionById(String id) {
        // TODO: 21/04/2023  
        return null;
    }

    public Iterable<TransactionDTO> findAll() {
        // TODO: 21/04/2023  
        return null;
    }
}
