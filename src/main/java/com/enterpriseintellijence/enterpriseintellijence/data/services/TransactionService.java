package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.TransactionDTO;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;

public interface TransactionService {
    TransactionDTO createTransaction(TransactionDTO transactionDTO);

    TransactionDTO replaceTransaction(String id, TransactionDTO transactionDTO);

    TransactionDTO updateTransaction(String id, JsonPatch patch) throws JsonPatchException;

    TransactionDTO deleteTransaction(String id);

    TransactionDTO transactionById(String id);

    Iterable<TransactionDTO> findAll();
}
