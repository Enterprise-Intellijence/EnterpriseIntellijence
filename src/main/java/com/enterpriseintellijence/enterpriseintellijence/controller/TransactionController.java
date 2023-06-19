package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.dto.TransactionDTO;
import com.enterpriseintellijence.enterpriseintellijence.data.services.TransactionService;

import com.enterpriseintellijence.enterpriseintellijence.dto.creation.TransactionCreateDTO;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/v1/transactions", produces="application/json")
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionDTO createTransaction(@Valid @RequestBody TransactionCreateDTO transactionDTO) throws IllegalAccessException {
        return transactionService.createTransaction(transactionDTO);
    }

    @PutMapping(path = "/{id}",consumes="application/json")
    public ResponseEntity<TransactionDTO> replaceTransaction(@PathVariable("id") String id, @Valid @RequestBody TransactionDTO transactionDTO) throws IllegalAccessException {
        return ResponseEntity.ok(transactionService.replaceTransaction(id,transactionDTO));
    }

    @PatchMapping(path="/{id}", consumes = "application/json")
    public ResponseEntity<TransactionDTO> updateTransaction(@PathVariable("id") String id, @Valid @RequestBody TransactionDTO patch) throws IllegalAccessException {
        return ResponseEntity.ok(transactionService.updateTransaction(id,patch));
    }

    @DeleteMapping(path="/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteTransaction(@PathVariable("id") String id) throws IllegalAccessException {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> transactionById(@PathVariable("id") String id){
        return ResponseEntity.ok(transactionService.transactionById(id));
    }

    @GetMapping("")
    public Iterable<TransactionDTO> allTransaction() {
        return transactionService.findAll();
    }
}
