package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.dto.ProductDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.TransactionDTO;
import com.enterpriseintellijence.enterpriseintellijence.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/v1/transactions", produces="application/json")
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionDTO createTransaction(@RequestBody TransactionDTO transactionDTO){
        return transactionService.createTransaction(transactionDTO);
    }

    @PutMapping(path = "/{id}",consumes="application/json")
    public ResponseEntity<TransactionDTO> replaceTransaction(@PathVariable("id") String id, @RequestBody TransactionDTO transactionDTO){
        return transactionService.replaceTransaction(transactionDTO);
    }

    @PatchMapping(path="/{id}", consumes = "application/json")
    public ResponseEntity<TransactionDTO> updateTransaction(@PathVariable("id") String id, @RequestBody TransactionDTO transactionDTO) {
        return transactionService.updateTransaction(transactionDTO);
    }

    @DeleteMapping(path="/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTransaction(@PathVariable("id") String id){
        transactionService.deleteTransaction(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> transactionById(@PathVariable("id") String id){
        return transactionService.transactionById(id);
    }

    @GetMapping("")
    public Iterable<TransactionDTO> allTransaction() {
        return transactionService.findAll();
    }
}
