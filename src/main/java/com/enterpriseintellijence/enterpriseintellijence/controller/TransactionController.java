package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.dto.TransactionDTO;
import com.enterpriseintellijence.enterpriseintellijence.data.services.TransactionService;

import com.enterpriseintellijence.enterpriseintellijence.dto.creation.TransactionCreateDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.enterpriseintellijence.enterpriseintellijence.security.AppSecurityConfig.SECURITY_CONFIG_NAME;

@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/v1/transactions", produces="application/json")
@SecurityRequirement(name = SECURITY_CONFIG_NAME)
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionDTO createTransaction(@Valid @RequestBody TransactionCreateDTO transactionDTO) throws IllegalAccessException {
        return transactionService.createTransaction(transactionDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> transactionById(@PathVariable("id") String id){
        return ResponseEntity.ok(transactionService.transactionById(id));
    }

    @GetMapping("")
    public Iterable<TransactionDTO> allTransaction() throws IllegalAccessException {
        return transactionService.findAll();
    }
}
