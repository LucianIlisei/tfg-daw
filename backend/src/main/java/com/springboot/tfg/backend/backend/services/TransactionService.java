package com.springboot.tfg.backend.backend.services;

import com.springboot.tfg.backend.backend.entities.Transaction;
import com.springboot.tfg.backend.backend.entities.User;
import com.springboot.tfg.backend.backend.repositories.TransactionRepository;
import com.springboot.tfg.backend.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    public Transaction saveTransaction(Transaction transaction, String username) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        transaction.setUser(user);
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactions(String username) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return transactionRepository.findByUser(user);
    }
}
