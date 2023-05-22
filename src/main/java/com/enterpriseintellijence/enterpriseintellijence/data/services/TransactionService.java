package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.TransactionDTO;


public interface TransactionService {
    TransactionDTO createTransaction(TransactionDTO transactionDTO) throws IllegalAccessException;

    TransactionDTO replaceTransaction(String id, TransactionDTO transactionDTO) throws IllegalAccessException;

    TransactionDTO updateTransaction(String id, TransactionDTO patch) throws IllegalAccessException;

    void deleteTransaction(String id) throws IllegalAccessException;

    TransactionDTO transactionById(String id);

    Iterable<TransactionDTO> findAll();
}
