package com.springboot.tfg.backend.backend.controllers;

import com.springboot.tfg.backend.backend.entities.Transaction;
import com.springboot.tfg.backend.backend.services.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "http://localhost:4200")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(
            @Valid @RequestBody Transaction transaction) {
        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Transaction saved = transactionService.saveTransaction(transaction, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getTransactions() {
        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        List<Transaction> transactions = transactionService.getTransactions(username);
        return ResponseEntity.ok(transactions);
    }

}
