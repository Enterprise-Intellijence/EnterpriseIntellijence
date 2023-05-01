package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.dto.TransactionDTO;
import com.enterpriseintellijence.enterpriseintellijence.data.services.TransactionService;

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

    private final Bandwidth limit = Bandwidth.classic(20, Refill.greedy(25, Duration.ofMinutes(1)));
    private final Bucket bucket = Bucket.builder().addLimit(limit).build();

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionDTO createTransaction(@Valid @RequestBody TransactionDTO transactionDTO){
        return transactionService.createTransaction(transactionDTO);
    }

    @PutMapping(path = "/{id}",consumes="application/json")
    public ResponseEntity<TransactionDTO> replaceTransaction(@PathVariable("id") String id, @Valid @RequestBody TransactionDTO transactionDTO){
        return ResponseEntity.ok(transactionService.replaceTransaction(id,transactionDTO));
    }

    @PatchMapping(path="/{id}", consumes = "application/json")
    public ResponseEntity<TransactionDTO> updateTransaction(@PathVariable("id") String id, @Valid @RequestBody TransactionDTO patch){
        return ResponseEntity.ok(transactionService.updateTransaction(id,patch));
    }

    @DeleteMapping(path="/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTransaction(@PathVariable("id") String id){
        transactionService.deleteTransaction(id);
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
