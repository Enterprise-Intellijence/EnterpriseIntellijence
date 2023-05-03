package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.TransactionDTO;


public interface TransactionService {
    TransactionDTO createTransaction(TransactionDTO transactionDTO);

    TransactionDTO replaceTransaction(String id, TransactionDTO transactionDTO);

    TransactionDTO updateTransaction(String id, TransactionDTO patch);

    void deleteTransaction(String id);

    TransactionDTO transactionById(String id);

    Iterable<TransactionDTO> findAll();
}
